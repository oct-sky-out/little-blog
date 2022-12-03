package com.example.reactivewebexample.category.service;

import com.example.reactivewebexample.category.document.Category;
import reactor.core.publisher.Mono;

public interface CategoryService {
    Mono<Category> addCategory(String categoryName);

    Mono<Void> updateCategory(String categoryId, String replaceName);
}
