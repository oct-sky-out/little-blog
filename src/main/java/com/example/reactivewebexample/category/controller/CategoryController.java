package com.example.reactivewebexample.category.controller;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

import com.example.reactivewebexample.category.dto.CategoryComposite;
import com.example.reactivewebexample.category.dto.CategorySaveDto;
import com.example.reactivewebexample.category.dto.HalCategories;
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
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/categories", produces = {MediaTypes.HAL_JSON_VALUE,
    MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<EntityModel<CreationDto>> createCategory(@RequestBody @Valid CategorySaveDto body,
                                                         @RequestParam(required = false)
                                                         String parentId) {
        Mono<CreationDto> createdCategory = categoryService.addCategory(body.name(), parentId);

        return createdCategory.flatMap(category -> {
            CategoryController controller = methodOn(CategoryController.class);
            Mono<Link> selfLink =
                linkTo(controller.createCategory(body, null)).slash(body.name()).withSelfRel()
                    .toMono()
                    .flatMap(link -> Mono.just(link.expand().withSelfRel()));
            Mono<Link> updateLink =
                linkTo(controller.updateCategory(category.id(), null)).withRel("update").toMono();
            Mono<Link> deleteLink =
                linkTo(controller.deleteCategory(category.id())).withRel("delete").toMono();

            return Mono.zip(selfLink, updateLink, deleteLink).flatMap(tuple -> Mono.just(
                EntityModel.of(category, tuple.getT1(), tuple.getT2(), tuple.getT3())));
        });
    }

    @PutMapping("/{categoryId}")
    public Mono<EntityModel<ModifyDto<CategorySaveDto>>> updateCategory(
        @PathVariable String categoryId, @RequestBody @Valid CategorySaveDto body) {
        CategoryController controller = methodOn(CategoryController.class);
        Mono<Link> selfLink =
            linkTo(controller.createCategory(body, null)).slash(body.name()).withSelfRel().toMono();

        return categoryService.updateCategory(categoryId, body.name()).zipWith(selfLink)
            .flatMap(o -> Mono.just(EntityModel.of(o.getT1(), o.getT2())));
    }

    @PatchMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteCategory(@PathVariable String categoryId) {
        return categoryService.deleteCategory(categoryId);
    }

    @GetMapping
    public Mono<CollectionModel<HalCategories>> retrieveAllCategories() {
        return categoryService.retrieveCategories().flatMap(CategoryComposite::toHal);
    }
}
