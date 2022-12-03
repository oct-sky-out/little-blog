package com.example.reactivewebexample.category.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.example.reactivewebexample.category.document.Category;
import com.example.reactivewebexample.category.dto.CategorySaveDto;
import com.example.reactivewebexample.category.service.CategoryService;
import com.example.reactivewebexample.category.service.CategoryServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@Slf4j
@ExtendWith(SpringExtension.class)
@Import({CategoryController.class, CategoryServiceImpl.class})
class CategoryControllerTest {
    @Autowired
    CategoryController controller;

    @MockBean
    CategoryService service;

    @Test
    @DisplayName("카테고리 생성요청을 받아야한다.")
    void 카테고리_생성요청을_받아야한다() {
        Category testCategory = new Category("example");
        ObjectId oid = new ObjectId();
        ReflectionTestUtils.setField(testCategory, "id", oid);

        given(service.addCategory(anyString()))
            .willReturn(Mono.just(testCategory));

        WebTestClient.bindToController(controller)
            .build()
            .post()
            .uri("/categories")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(new CategorySaveDto("example"))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody()
            .consumeWith(entityExchangeResult -> log.debug("body: {}", new String(entityExchangeResult.getResponseBody())))
            .jsonPath("$.id", oid.toHexString()).exists()
            .jsonPath("$.name", testCategory.getName()).exists()
            .jsonPath("$.baseField", testCategory.getBaseField()).exists()
            .jsonPath("$.children").doesNotExist()
            .jsonPath("$.links[0].rel", "self").exists()
            .jsonPath("$.links[0].href", "/categories/example").exists()
            .returnResult();
    }

    @Test
    @DisplayName("카테고리 수정요청을 받아야한다.")
    void 카테고리_수정_요청을_받아들인다() {
        ObjectId oid = new ObjectId();

        given(service.updateCategory(oid.toHexString(), "replaceName"))
            .willReturn(Mono.empty());

        WebTestClient.bindToController(controller)
            .build()
            .put()
            .uri("/categories/" + oid.toHexString())
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(new CategorySaveDto("replaceName"))
            .exchange()
            .expectStatus()
            .isNoContent()
            .expectBody()
            .returnResult();

        then(service)
            .should()
            .updateCategory(oid.toHexString(), "replaceName");
    }
}
