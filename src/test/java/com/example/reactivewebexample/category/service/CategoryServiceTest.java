package com.example.reactivewebexample.category.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.example.reactivewebexample.category.document.Category;
import com.example.reactivewebexample.category.dto.Categories;
import com.example.reactivewebexample.category.dto.CategoryComposite;
import com.example.reactivewebexample.category.dto.CategorySaveDto;
import com.example.reactivewebexample.category.dto.HalCategories;
import com.example.reactivewebexample.category.repository.CategoryRepository;
import com.example.reactivewebexample.common.dto.CreationDto;
import com.example.reactivewebexample.common.dto.ModifyDto;
import java.util.Objects;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.CollectionModel;
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
    @DisplayName("카테고리를 삭제할 수 있다.")
    void 카테고리를_삭제한다() {
        ObjectId oid = new ObjectId();
        Category testCategory = new Category("example");

        ReflectionTestUtils.setField(testCategory, "id", oid);

        given(repository.findById(oid))
            .willReturn(Mono.just(testCategory));
        given(repository.countAllChildrenByParentId(anyString()))
            .willReturn(Mono.just(0L));
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
    void 카테고리를_삭제시_자식_카테고리가_있으면_삭제할_수_없다() {
        ObjectId oid = new ObjectId();
        ObjectId childOid = new ObjectId();
        Category testCategory = new Category("example");
        Category testChildCategory = new Category("example");

        ReflectionTestUtils.setField(testChildCategory, "id", childOid);
        ReflectionTestUtils.setField(testCategory, "id", oid);
        ReflectionTestUtils.setField(testCategory, "id", oid);

        String errorMsg = "자식 카테고리가 존재합니다. 자식 카테고리를 삭제하고 다시 시도해주세요.";

        given(repository.findById(oid))
            .willReturn(Mono.just(testCategory));
        given(repository.countAllChildrenByParentId(oid.toString()))
            .willReturn(Mono.just(1L));
        given(repository.delete(testCategory))
            .willThrow(new RuntimeException(errorMsg));

        Mono<Void> deleted = service.deleteCategory(oid.toHexString());

        StepVerifier.create(deleted)
            .expectErrorSatisfies(throwable -> {
                assertThat(throwable).isInstanceOf(RuntimeException.class);
                assertThat(throwable.getMessage()).isEqualTo(errorMsg);
            })
            .log()
            .verify();

        then(repository)
            .should(times(1))
            .findById(oid);
        then(repository)
            .should(times(1))
            .countAllChildrenByParentId(oid.toString());
        then(repository)
            .should(times(0))
            .delete(any());

    }

    @Test
    void 카테고리를_모두_조회한다1() {
        Category c = new Category("example");
        Category c1 =new Category("example1");
        Category c2 =new Category("example2");
        ReflectionTestUtils.setField(c, "id", new ObjectId());
        ReflectionTestUtils.setField(c1, "id", new ObjectId());
        ReflectionTestUtils.setField(c2, "id", new ObjectId());

        given(repository.findAll()).willReturn(Flux.just(c,c1,c2));

        Mono<CollectionModel<HalCategories>>
            categoryFlux = service.retrieveCategories().flatMap(CategoryComposite::toHal)
            .log();

        StepVerifier.create(categoryFlux)
            .expectSubscription()
            .consumeNextWith(composite ->
                assertThat(composite.getContent()).isNotEmpty().hasSize(3))
            .verifyComplete();

        then(repository)
            .should()
            .findAll();
    }

    @Test
    @DisplayName("카테고리를 모두 조회 할 수 있다.")
    void 카테고리를_모두_조회한다2() {
        Category c = new Category("example");
        Category c1 =new Category("example1");
        Category c2 =new Category("example2");
        Category c3 =new Category("example3");
        Category c4 =new Category("example4");
        Category c5 =new Category("example5");
        Category c6 =new Category("example6");
        Category c7 =new Category("example7");
        Category c8 =new Category("example8");
        ObjectId cOid = new ObjectId();
        ObjectId c5Oid = new ObjectId();
        ReflectionTestUtils.setField(c, "id", cOid);
        ReflectionTestUtils.setField(c1, "id", new ObjectId());
        ReflectionTestUtils.setField(c2, "id", new ObjectId());
        ReflectionTestUtils.setField(c3, "id", new ObjectId());
        ReflectionTestUtils.setField(c4, "id", new ObjectId());
        ReflectionTestUtils.setField(c5, "id", c5Oid);
        ReflectionTestUtils.setField(c6, "id", new ObjectId());
        ReflectionTestUtils.setField(c7, "id", new ObjectId());
        ReflectionTestUtils.setField(c8, "id", new ObjectId());

        ReflectionTestUtils.setField(c1, "parentId", cOid);
        ReflectionTestUtils.setField(c2, "parentId", cOid);
        ReflectionTestUtils.setField(c3, "parentId", cOid);
        ReflectionTestUtils.setField(c4, "parentId", cOid);
        ReflectionTestUtils.setField(c6, "parentId", c5Oid);
        ReflectionTestUtils.setField(c7, "parentId", c5Oid);
        ReflectionTestUtils.setField(c8, "parentId", c5Oid);

        Categories categories = new Categories(c);
        categories.addChildCategory(c1);
        categories.addChildCategory(c2);
        categories.addChildCategory(c3);
        categories.addChildCategory(c4);
        Categories categories2 = new Categories(c5);
        categories2.addChildCategory(c6);
        categories2.addChildCategory(c7);
        categories2.addChildCategory(c8);

        given(repository.findAll()).willReturn(Flux.just(c,c1,c2,c3,c4,c5,c6,c7,c8));

        Mono<CollectionModel<HalCategories>>
            categoryFlux = service.retrieveCategories().flatMap(CategoryComposite::toHal)
            .log();

        StepVerifier.create(categoryFlux)
            .expectSubscription()
            .consumeNextWith(composite ->
                assertThat(composite.getContent()).isNotEmpty().hasSize(2))
            .verifyComplete();

        then(repository)
            .should()
            .findAll();
    }
}
