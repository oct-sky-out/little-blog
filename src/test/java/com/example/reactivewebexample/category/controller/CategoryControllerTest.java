package com.example.reactivewebexample.category.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.hateoas.MediaTypes;
import com.example.reactivewebexample.category.document.Category;
import com.example.reactivewebexample.category.dto.CategorySaveDto;
import com.example.reactivewebexample.category.service.CategoryService;
import com.example.reactivewebexample.common.dto.CreationDto;
import com.example.reactivewebexample.common.dto.ModifyDto;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@WebFluxTest(CategoryController.class)
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

        given(service.addCategory("example", null)).willReturn(
            Mono.just(new CreationDto(oid.toHexString())));

        WebTestClient.bindToController(controller)
            .build()
            .post()
            .uri("/api/categories")
            .contentType(MediaTypes.HAL_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(new CategorySaveDto("example")).exchange()
            .expectStatus()
            .isCreated()
            .expectBody()
            .consumeWith(entityExchangeResult -> log.debug("body: {}", new String(entityExchangeResult.getResponseBody())))
            .jsonPath("$.id", oid.toHexString()).exists()
            .jsonPath("$.links[0].rel", "self").exists()
            .jsonPath("$.links[1].rel", "update").exists()
            .jsonPath("$.links[2].rel", "delete").exists()
            .jsonPath("$.links[0].href", "/categories/example").exists()
            .jsonPath("$.links[1].href", "/categories/example").exists()
            .jsonPath("$.links[2].href", "/categories/example").exists()
            .returnResult();
    }

    @Test
    @DisplayName("카테고리 수정요청을 받아야한다.")
    void 카테고리_수정_요청을_받아들인다() throws JsonProcessingException {
        Category testCategory = new Category("example");
        ObjectId oid = new ObjectId();
        ReflectionTestUtils.setField(testCategory, "id", oid);


        given(service.updateCategory(oid.toHexString(), "replaceName")).willReturn(
            ModifyDto.toMono(oid.toHexString(), new CategorySaveDto(testCategory.getName())));

        WebTestClient.bindToController(controller)
            .build()
            .put()
            .uri("/api/categories/" + oid.toHexString()).accept(MediaType.APPLICATION_JSON)
            .contentType(MediaTypes.HAL_JSON)
            .bodyValue(
                new ObjectMapper()
                    .registerModule(new Jackson2HalModule())
                    .writeValueAsString(new CategorySaveDto("replaceName")))
            .exchange().expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .consumeWith(entityExchangeResult -> log.debug("body: {}", new String(entityExchangeResult.getResponseBody())))
            .jsonPath("$.id", oid.toHexString()).exists()
            .jsonPath("$.diff.name", "replaceName").exists()
            .jsonPath("$.links[0].rel", "self").exists()
            .jsonPath("$.links[0].href", "/categories/replaceName").exists()
            .returnResult();

        then(service).should().updateCategory(oid.toHexString(), "replaceName");
    }

    @Test
    @DisplayName("카테고리 삭제요청을 받은 후 처리한다. (부모)")
    void 카테고리_삭제요청을_받는다() {
        Category testCategory = new Category("example");
        ObjectId oid = new ObjectId();
        ReflectionTestUtils.setField(testCategory, "id", oid);

        given(service.deleteCategory(anyString())).willReturn(Mono.empty());

        WebTestClient.bindToController(controller).build().patch()
            .uri("/api/categories/" + oid.toHexString())
            .accept(MediaType.APPLICATION_JSON, MediaTypes.HAL_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        then(service)
            .should()
            .deleteCategory(anyString());
    }

    @Test
    @DisplayName("카테고리의 전체조회 요청을 받은 후 처리한다.")
    void 카테고리_전체조회_요청을_받는다() {
        List<Category> categories = List.of(new Category("example"),
            new Category("example1"),
            new Category("example2"),
            new Category("example3"),
            new Category("example4"),
            new Category("example5"));

        Flux<Category> categoryFlux = Flux.fromIterable(categories);

        given(service.retrieveCategories())
            .willReturn(categoryFlux);

        categories.forEach(category -> ReflectionTestUtils.setField(category, "id", new ObjectId()));

        WebTestClient.bindToController(controller)
            .build()
            .get()
            .uri("/api/categories")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .consumeWith(entityExchangeResult -> log.debug("body: {}", new String(entityExchangeResult.getResponseBody())))
            .jsonPath("$.content").isArray()
            .jsonPath("$.content[0].name", "example").exists()
            .jsonPath("$.content[0].links[0].href", "/api/categories/example").exists()
            .jsonPath("$.links[0].href", "/api/categories").exists()
            .returnResult();

        then(service)
            .should()
            .retrieveCategories();
    }

    @Test
    @DisplayName("자식 카테고리의 생성요청을 받는다.")
    void 자식_카테고리의_생성요청을_받아들인다() {
        Category testCategory = new Category("example");
        ObjectId oid = new ObjectId();
        ObjectId parentOid = new ObjectId();
        ReflectionTestUtils.setField(testCategory, "id", oid);

        given(service.addCategory("example", parentOid.toString())).willReturn(
            Mono.just(new CreationDto(oid.toHexString())));

        WebTestClient.bindToController(controller)
            .build()
            .post()
            .uri(uriBuilder ->
                uriBuilder.path("/api/categories")
                    .queryParam("parentId", parentOid.toString())
                    .build())
            .contentType(MediaTypes.HAL_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(new CategorySaveDto("example")).exchange()
            .expectStatus()
            .isCreated()
            .expectBody()
            .consumeWith(entityExchangeResult -> log.debug("body: {}", new String(entityExchangeResult.getResponseBody())))
            .jsonPath("$.id", oid.toHexString()).exists()
            .jsonPath("$.links[0].rel", "self").exists()
            .jsonPath("$.links[1].rel", "update").exists()
            .jsonPath("$.links[2].rel", "delete").exists()
            .jsonPath("$.links[0].href", "/categories/example").exists()
            .jsonPath("$.links[1].href", "/categories/example").exists()
            .jsonPath("$.links[2].href", "/categories/example").exists()
            .returnResult();
    }
}
