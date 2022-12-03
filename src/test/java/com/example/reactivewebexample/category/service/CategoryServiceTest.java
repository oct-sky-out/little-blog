package com.example.reactivewebexample.category.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.example.reactivewebexample.category.document.Category;
import com.example.reactivewebexample.category.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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

        given(repository.insert(any(Category.class)))
            .willReturn(Mono.just(categoryTestObj));

        Mono<Category> category = service.addCategory(categoryName);

        StepVerifier.create(category)
            .expectSubscription()
            .expectNextMatches(category1 -> category1.equals(categoryTestObj))
            .verifyComplete();
    }
}
