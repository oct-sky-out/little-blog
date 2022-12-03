package com.example.reactivewebexample.category.service;

import com.example.reactivewebexample.category.document.Category;
import com.example.reactivewebexample.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public Mono<Category> addCategory(String categoryName) {
        return categoryRepository.insert(new Category(categoryName));
    }
}
