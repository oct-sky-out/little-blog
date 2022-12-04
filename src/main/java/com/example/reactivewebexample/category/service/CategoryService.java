package com.example.reactivewebexample.category.service;

import com.example.reactivewebexample.category.document.Category;
import com.example.reactivewebexample.category.dto.CategorySaveDto;
import com.example.reactivewebexample.common.dto.CreationDto;
import com.example.reactivewebexample.common.dto.ModifyDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CategoryService {
    Mono<CreationDto> addCategory(String categoryName);

    Mono<ModifyDto<CategorySaveDto>> updateCategory(String categoryId, String replaceName);

    Mono<Void> deleteCategory(String categoryId);

    Flux<Category> retrieveCategories();
}
