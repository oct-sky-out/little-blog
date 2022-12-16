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

        // update와 delete HAL 관계를 지정한다.
        Mono<Link> updateLink = linkTo(controller.updateCategory(categoryId,null))
            .withRel("update")
            .toMono();
        Mono<Link> deleteLink = linkTo(controller.deleteCategory(categoryId))
            .withRel("delete")
            .toMono();
        Mono<Category> categoryMono = Mono.just(categories.category);

        // 카테고리의 HAL화 작업을 수행한다.
        return Mono.zip(updateLink, deleteLink, categoryMono)
            .flatMap(tuple -> Mono.just(EntityModel.of(tuple.getT3(), tuple.getT1(), tuple.getT2())));
    }

    public Mono<List<HalCategories>> parseChildrenHal(Categories categories) {
        // 우선은 자식 카테고리의 Category에 HAL화 시킨다.
        return Flux.fromIterable(categories.children)
            .flatMap(categories1 -> {
                Mono<EntityModel<Category>> categoryEntityModel =
                    parseCategoryHal(categories1);

                /*
                 * 그리고 손자 카테고리들이 존재할 경우, parseChildrenHal을 통해
                 * 손자 카테고리 또한 HAL화 시키고 자식 카테고리와 함께 병합시켜 내보낸다.
                 */
                if(Objects.nonNull(categories1.getChildren())) {
                    Mono<List<HalCategories>> grandChildCategories = parseChildrenHal(categories1);
                    return grandChildCategories.zipWith(categoryEntityModel)
                        .flatMap(tuple -> // T2 -> 자식 카테고리 T1 -> 손자 카테고리
                            Mono.just(new HalCategories(tuple.getT2(), tuple.getT1())));
                }

                // 손자 카테고리가 없는 경우, 바로 HalCategories로 만들어서 내보낸다.
                return categoryEntityModel
                    .flatMap(entityModel ->
                        Mono.just(new HalCategories(entityModel, null))).flux();
            })
            .collectList();
    }
}
