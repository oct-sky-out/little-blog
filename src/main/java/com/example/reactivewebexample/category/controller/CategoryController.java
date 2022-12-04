package com.example.reactivewebexample.category.controller;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

import com.example.reactivewebexample.category.dto.CategorySaveDto;
import com.example.reactivewebexample.category.service.CategoryService;
import com.example.reactivewebexample.common.dto.CreationDto;
import com.example.reactivewebexample.common.dto.ModifyDto;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<EntityModel<CreationDto>> createCategory(
        @RequestBody @Valid CategorySaveDto body) {

        CategoryController controller = methodOn(CategoryController.class);

        Mono<Link> selfLink = linkTo(controller.createCategory(body)).slash(body.name())
            .withSelfRel().toMono();

        return categoryService.addCategory(body.name())
            .zipWith(selfLink)
            .flatMap(o -> Mono.just(EntityModel.of(o.getT1(), o.getT2())));
    }

    @PutMapping("/{categoryId}")
    public Mono<EntityModel<ModifyDto<CategorySaveDto>>> updateCategory(@PathVariable String categoryId,
                                          @RequestBody @Valid CategorySaveDto body) {
        CategoryController controller = methodOn(CategoryController.class);
        Mono<Link> selfLink = linkTo(controller.createCategory(body)).slash(body.name())
            .withSelfRel().toMono();

        return categoryService.updateCategory(categoryId, body.name())
            .zipWith(selfLink)
            .flatMap(o -> Mono.just(EntityModel.of(o.getT1(), o.getT2())));
    }

}
