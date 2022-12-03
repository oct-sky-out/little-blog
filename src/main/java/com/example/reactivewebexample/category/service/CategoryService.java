package com.example.reactivewebexample.category.service;

import com.example.reactivewebexample.common.dto.CreationDto;
import reactor.core.publisher.Mono;

public interface CategoryService {
    Mono<CreationDto> addCategory(String categoryName);

    Mono<Void> updateCategory(String categoryId, String replaceName);
}
