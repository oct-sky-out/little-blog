package com.example.reactivewebexample.category.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.example.reactivewebexample.category.document.Category;
import com.example.reactivewebexample.category.dto.CategorySaveDto;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
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

        Mono<CreationDto> category = service.addCategory(categoryName);

        StepVerifier.create(category)
            .expectSubscription()
            .expectNextMatches(creationDto -> creationDto.id().equals(oid.toHexString()))
            .verifyComplete();
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
}
