package com.example.reactivewebexample.category.service;

import com.example.reactivewebexample.category.document.Category;
import com.example.reactivewebexample.category.dto.CategorySaveDto;
import com.example.reactivewebexample.category.repository.CategoryRepository;
import com.example.reactivewebexample.common.dto.CreationDto;
import com.example.reactivewebexample.common.dto.ModifyDto;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    @Override
    public Mono<CreationDto> addCategory(String categoryName, String parentId) {
        return Mono.defer(() -> {
            Mono<Category> category = Mono.just(new Category(categoryName));

            if(Objects.nonNull(parentId)) {
                categoryRepository.findById(new ObjectId(parentId))
                    .switchIfEmpty(Mono.error(new RuntimeException("알 수 없는 카테고리입니다.")))
                    .zipWith(category)
                    .subscribe(categoryTuple ->  // T1 - parent, T2 - child
                        categoryTuple.getT2().addParent(categoryTuple.getT1()));
            }

            return category;
            })
        .flatMap(categoryRepository::insert)
        .flatMap(CategoryServiceImpl::categoryCreationSuccess);
    }

    @NotNull
    private static Mono<CreationDto> categoryCreationSuccess(Category category) {
        return Mono.just(new CreationDto(category.getId().toHexString()));
    }

    @Transactional
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

    @Transactional
    @Override
    public Mono<Void> deleteCategory(String categoryId) {
        return categoryRepository.findById(new ObjectId(categoryId))
            .switchIfEmpty(Mono.error(new RuntimeException("카테고리가 존재하지 않습니다.")))
            .flatMap(categoryRepository::delete)
            .then();
    }

    @Transactional(readOnly = true)
    @Override
    public Flux<Category> retrieveCategories() {
        return categoryRepository.findAll();
    }

}
