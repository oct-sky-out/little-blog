package com.example.reactivewebexample.category.service;

import com.example.reactivewebexample.category.document.Category;
import com.example.reactivewebexample.category.dto.Categories;
import com.example.reactivewebexample.category.dto.CategoryComposite;
import com.example.reactivewebexample.category.dto.CategorySaveDto;
import com.example.reactivewebexample.category.repository.CategoryRepository;
import com.example.reactivewebexample.common.dto.CreationDto;
import com.example.reactivewebexample.common.dto.ModifyDto;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Slf4j
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
            .zipWith(categoryRepository.countAllChildrenByParentId(categoryId))
            .flatMap(o -> { // o.T1 = delete category, o.T2 = children count
                if(o.getT2() > 0) {
                    return Mono.error(
                        new RuntimeException("자식 카테고리가 존재합니다. 자식 카테고리를 삭제하고 다시 시도해주세요."));
                }
                return categoryRepository.delete(o.getT1());
            })
            .then();
    }

    @Transactional(readOnly = true)
    @Override
    public Mono<CategoryComposite> retrieveCategories() {
        return categoryRepository.findAll()
            .reduce(new CategoryComposite(), (composite, category) -> {
                if(Objects.nonNull(category.getParentId())) {
                    log.error("category name :{}", category.getName());
                    log.error("composite status :{}", composite);
                    composite.findParentCategory(category.getParentId())
                        .ifPresent(categories -> categories.addChildCategory(category));
                    return composite;
                }
                composite.addCategoryComposite(new Categories(category));
                return composite;
            });
    }

}
