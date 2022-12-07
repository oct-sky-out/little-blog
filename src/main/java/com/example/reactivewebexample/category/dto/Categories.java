package com.example.reactivewebexample.category.dto;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

import com.example.reactivewebexample.category.controller.CategoryController;
import com.example.reactivewebexample.category.document.Category;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Getter
public class Categories {
    private final Category category;
    private List<Categories> children;

    public Categories(Category category) {
        this.category = category;
    }

    public void addChildCategory(Category category) {
        if(Objects.isNull(this.children)) {
            this.children = new ArrayList<>();
        }

        this.children.add(new Categories(category));
    }

    public Mono<EntityModel<Category>> parseCategoryHal(Categories categories) {
        CategoryController controller = methodOn(CategoryController.class);
        String categoryId = categories.category.getId().toHexString();

        Mono<Link> updateLink = linkTo(controller.updateCategory(categoryId,null))
            .withRel("update")
            .toMono();
        Mono<Link> deleteLink = linkTo(controller.deleteCategory(categoryId))
            .withRel("delete")
            .toMono();
        Mono<Category> categoryMono = Mono.just(categories.category);

        return Mono.zip(updateLink, deleteLink, categoryMono)
            .flatMap(tuple -> Mono.just(EntityModel.of(tuple.getT3(), tuple.getT1(), tuple.getT2())));
    }

    public Mono<List<HalCategories>> parseChildrenHal(Categories categories) {
        return Flux.fromIterable(categories.children)
            .flatMap(categories1 -> {
                Mono<EntityModel<Category>> categoryEntityModel =
                    parseCategoryHal(categories1);

                if(Objects.nonNull(categories1.getChildren())) {
                    Mono<List<HalCategories>> halCategoriesFlux = parseChildrenHal(categories1);
                    return halCategoriesFlux.zipWith(categoryEntityModel)
                        .flatMap(tuple ->
                            Mono.just(new HalCategories(tuple.getT2(), tuple.getT1())));
                }

                return categoryEntityModel
                    .flatMap(entityModel ->
                        Mono.just(new HalCategories(entityModel, null))).flux();
            })
            .collectList();
    }
}
