package com.example.reactivewebexample.category.service;

import com.example.reactivewebexample.category.dto.CategoryComposite;
import com.example.reactivewebexample.category.dto.CategorySaveDto;
import com.example.reactivewebexample.common.dto.CreationDto;
import com.example.reactivewebexample.common.dto.ModifyDto;
import reactor.core.publisher.Mono;

public interface CategoryService {
    Mono<CreationDto> addCategory(String categoryName, String parentId);

    Mono<ModifyDto<CategorySaveDto>> updateCategory(String categoryId, String replaceName);

    Mono<Void> deleteCategory(String categoryId);

    Mono<CategoryComposite> retrieveCategories();
}
