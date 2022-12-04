package com.example.reactivewebexample.category.service;

import com.example.reactivewebexample.category.document.Category;
import com.example.reactivewebexample.category.dto.CategorySaveDto;
import com.example.reactivewebexample.category.repository.CategoryRepository;
import com.example.reactivewebexample.common.dto.CreationDto;
import com.example.reactivewebexample.common.dto.ModifyDto;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public Mono<CreationDto> addCategory(String categoryName) {
        return categoryRepository.insert(new Category(categoryName))
            .flatMap(category -> Mono.just(new CreationDto(category.getId().toHexString())));
    }

    @Override
    public Mono<ModifyDto<CategorySaveDto>> updateCategory(String categoryId, String replaceName) {
        return categoryRepository.findById(new ObjectId(categoryId))
            .switchIfEmpty(Mono.error(new RuntimeException("카테고리가 존재하지 않습니다.")))
            .flatMap(category -> {
                category.setName(replaceName);
                return Mono.just(category);
            })
            .flatMap(categoryRepository::save)
            .flatMap(category -> ModifyDto.toMono(
                category.getId().toHexString(), new CategorySaveDto(category.getName())));
    }

}
