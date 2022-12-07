package com.example.reactivewebexample.category.dto;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

import com.example.reactivewebexample.category.controller.CategoryController;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.hateoas.CollectionModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Getter
@ToString
public class CategoryComposite {
    private final List<Categories> categoryComposite = new ArrayList<>();

    public void addCategoryComposite(Categories categories) { // 부모 카테고리 추가
        this.categoryComposite.add(categories);
    }

    public Mono<CollectionModel<HalCategories>> toHal() {
        CategoryController controller = methodOn(CategoryController.class);
        return Flux.fromIterable(this.categoryComposite)
            .flatMap(categories ->
                Mono.zip(categories.parseCategoryHal(categories),
                    categories.parseChildrenHal(categories))
                .flatMap(tuple -> Mono.just(new HalCategories(tuple.getT1(), tuple.getT2()))))
            .collectList()
            .zipWith(linkTo(controller.retrieveAllCategories()).withSelfRel().toMono())
            .flatMap(o -> Mono.just(CollectionModel.of(o.getT1(), o.getT2())));
    }

    public Optional<Categories> findParentCategory(ObjectId parentId) {
        return categoryComposite.stream()
            .map(categories -> findParentCategory(parentId, categories))
            .findAny()
            .orElseThrow(RuntimeException::new);
    }

    private Optional<Categories> findParentCategory(ObjectId parentId, Categories categories) {
        ObjectId parentObjectId = categories.getCategory().getId();
        if(parentObjectId.equals(parentId)) {
            return Optional.of(categories);
        }

        if(Objects.nonNull(categories.getChildren())) {
            return categories.getChildren().stream()
                .map(categories1 -> findParentCategory(parentId, categories1))
                .filter(Objects::nonNull)
                .findAny()
                .orElse(Optional.empty());
        }

        return Optional.empty();
    }


}
