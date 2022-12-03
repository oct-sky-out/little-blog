package com.example.reactivewebexample.category.service;

import com.example.reactivewebexample.category.document.Category;
import com.example.reactivewebexample.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
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

    @Override
    public Mono<Void> updateCategory(String categoryId, String replaceName) {
        return categoryRepository.findById(new ObjectId(categoryId))
            .flatMap(category -> {
                category.setName(replaceName);
                return Mono.just(category);
            })
            .flatMap(categoryRepository::save)
            .then();
    }
}
