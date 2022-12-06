package com.example.reactivewebexample.category.controller;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

import com.example.reactivewebexample.category.document.Category;
import com.example.reactivewebexample.category.dto.CategorySaveDto;
import com.example.reactivewebexample.category.service.CategoryService;
import com.example.reactivewebexample.common.dto.CreationDto;
import com.example.reactivewebexample.common.dto.ModifyDto;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value="/api/categories", produces = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<EntityModel<CreationDto>> createCategory(
        @RequestBody @Valid CategorySaveDto body,
        @RequestParam(value = "parentId", required = false) String parentId) {

        CategoryController controller = methodOn(CategoryController.class);

        Mono<Link> selfLink = linkTo(controller.createCategory(body, parentId)).slash(body.name())
            .withSelfRel().toMono();

        return categoryService.addCategory(body.name(), parentId)
            .zipWith(selfLink)
            .flatMap(o -> Mono.just(EntityModel.of(o.getT1(), o.getT2())));
    }

    @PutMapping("/{categoryId}")
    public Mono<EntityModel<ModifyDto<CategorySaveDto>>> updateCategory(@PathVariable String categoryId,
                                          @RequestBody @Valid CategorySaveDto body) {
        CategoryController controller = methodOn(CategoryController.class);
        Mono<Link> selfLink = linkTo(controller.createCategory(body, null)).slash(body.name())
            .withSelfRel().toMono();

        return categoryService.updateCategory(categoryId, body.name())
            .zipWith(selfLink)
            .flatMap(o -> Mono.just(EntityModel.of(o.getT1(), o.getT2())));
    }

    @PatchMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteCategory(@PathVariable String categoryId) {
        return categoryService.deleteCategory(categoryId);
    }

    @GetMapping
    public Mono<CollectionModel<EntityModel<Category>>> retrieveAllCategories() {
        CategoryController controller = methodOn(CategoryController.class);
        // 하위 카테고리들은 EntityModel을 붙이려면 어떻게 할 것인가?
        // 일단 그냥 최상위만 붙이고 자식 카테고리를 개발할 때 생각하자.
        Flux<Category> categoriesFlux = categoryService.retrieveCategories();

        Flux<EntityModel<Category>> categoryResource = categoriesFlux.flatMap(category -> {
            String categoryId = category.getId().toHexString();

            return linkTo(controller.updateCategory(categoryId,null)).withRel("update").toMono()
                .zipWith(linkTo(controller.deleteCategory(categoryId)).withRel("delete").toMono())
                .flatMap(links -> Mono.just(EntityModel.of(category, links.getT1(), links.getT2())));
        });

        Mono<Link> selfLink = linkTo(controller.retrieveAllCategories()).withSelfRel().toMono();

        return categoryResource.collectList()
            .zipWith(selfLink)
            .flatMap(o -> Mono.just(CollectionModel.of(o.getT1(), o.getT2())));
    }
}
