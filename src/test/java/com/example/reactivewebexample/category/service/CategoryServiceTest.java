package com.example.reactivewebexample.category.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.example.reactivewebexample.category.document.Category;
import com.example.reactivewebexample.category.dto.CategorySaveDto;
import com.example.reactivewebexample.category.repository.CategoryRepository;
import com.example.reactivewebexample.common.dto.CreationDto;
import com.example.reactivewebexample.common.dto.ModifyDto;
import java.util.List;
import java.util.Objects;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@Import({CategoryServiceImpl.class, CategoryRepository.class})
class CategoryServiceTest {
    @Autowired
    private CategoryService service;

    @MockBean
    private CategoryRepository repository;


    @Test
    @DisplayName("카테고리를_생성한다")
    void 카테고리를_생성한다() {
        String categoryName = "example";
        Category categoryTestObj = new Category(categoryName);
        ObjectId oid = new ObjectId();

        ReflectionTestUtils.setField(categoryTestObj, "id", oid);

        given(repository.insert(any(Category.class)))
            .willReturn(Mono.just(categoryTestObj));

        Mono<CreationDto> category = service.addCategory(categoryName, null);

        StepVerifier.create(category)
            .expectSubscription()
            .expectNextMatches(creationDto -> creationDto.id().equals(oid.toHexString()))
            .verifyComplete();
    }

    @Test
    @DisplayName("자식카테고리를 생성한다.")
    void 자식_카테고리를_생성한다() {
        String categoryName = "example";
        String childCategoryName = "child";
        Category categoryTestObj = new Category(categoryName);
        Category childCategoryTestObj = new Category(childCategoryName);
        ObjectId parentOid = new ObjectId();
        ObjectId childOid = new ObjectId();

        ReflectionTestUtils.setField(categoryTestObj, "id", parentOid);
        ReflectionTestUtils.setField(childCategoryTestObj, "id", childOid);

        given(repository.findById(any(ObjectId.class)))
            .willReturn(Mono.just(categoryTestObj));
        given(repository.insert(any(Category.class)))
            .willReturn(Mono.just(childCategoryTestObj));

        Mono<CreationDto> category = service.addCategory(childCategoryName, parentOid.toHexString());

        StepVerifier.create(category)
            .expectSubscription()
            .expectNextMatches(creationDto -> creationDto.id().equals(childOid.toHexString()))
            .verifyComplete();

        then(repository)
            .should()
            .findById(parentOid);
        then(repository)
            .should()
            .insert(any(Category.class));
    }

    @Test
    void 카테고리를_수정한다() {
        String categoryName = "example";
        Category categoryTestObj = new Category(categoryName);
        Category updatedTestObj = new Category("replaceName");
        ObjectId oid = new ObjectId();

        ReflectionTestUtils.setField(categoryTestObj, "id", oid);
        ReflectionTestUtils.setField(updatedTestObj, "id", oid);

        given(repository.findById(any(ObjectId.class)))
            .willReturn(Mono.just(categoryTestObj));

        given(repository.save(categoryTestObj))
            .willReturn(Mono.just(updatedTestObj));

        Mono<ModifyDto<CategorySaveDto>> updated =
            service.updateCategory(oid.toHexString(), "replaceName")
            .flatMap(categorySaveDtoModifyDto -> {
                if(Objects.isNull(categorySaveDtoModifyDto)) {
                    return Mono.empty();
                }

                return Mono.just(categorySaveDtoModifyDto);

            });

        StepVerifier.create(updated)
            .expectSubscription()
            .assertNext(categorySaveDtoModifyDto -> {
                assertThat(categorySaveDtoModifyDto.getId()).isEqualTo(oid.toHexString());

                assertThat(categorySaveDtoModifyDto.getDiff())
                    .isEqualTo(new CategorySaveDto(categoryTestObj.getName()));
            })
            .verifyComplete();
    }

    @Test
    @DisplayName("자식 카테고리를 수정한다")
    @Disabled
    void 자식_카테고리를_수정한다() {
    }

    @Test
    @DisplayName("카테고리를 삭제할 수 있다.")
    void 카테고리를_삭제한다() {
        ObjectId oid = new ObjectId();
        Category testCategory = new Category("example");

        ReflectionTestUtils.setField(testCategory, "id", oid);

        given(repository.findById(oid))
            .willReturn(Mono.just(testCategory));
        given(repository.delete(testCategory))
            .willReturn(Mono.empty());

        Mono<Void> deleted = service.deleteCategory(oid.toHexString());

        StepVerifier.create(deleted)
            .expectSubscription()
            .expectNextCount(0)
            .verifyComplete();

        then(repository)
            .should()
            .findById(oid);
    }

    @Test
    @DisplayName("카테고리를 삭제시 자식 카테고리가 존재하면 삭제가 불가능하다.")
    @Disabled
    void 카테고리를_삭제시_자식_카테고리가_있으면_삭제할_수_없다() {

    }

    @Test
    @DisplayName("카테고리를 모두 조회 할 수 있다.")
    void 카테고리를_모두_조회한다() {
        List<Category> testCategories = List.of(new Category("example"),
            new Category("example1"),
            new Category("example2"),
            new Category("example3"),
            new Category("example4"),
            new Category("example5"));

        given(repository.findAll())
            .willReturn(Flux.fromIterable(testCategories));

        Flux<Category> categoryFlux = service.retrieveCategories();

        StepVerifier.create(categoryFlux)
            .expectSubscription()
            .expectNextCount(6)
            .verifyComplete();

        then(repository)
            .should()
            .findAll();
    }
}
