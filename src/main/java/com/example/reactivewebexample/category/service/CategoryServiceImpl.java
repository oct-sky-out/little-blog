package com.example.reactivewebexample.category.service;

import com.example.reactivewebexample.category.document.Category;
import com.example.reactivewebexample.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public Mono<Category> addCategory(String categoryName) {
        return categoryRepository.insert(new Category(categoryName));
    }
}
