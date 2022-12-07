package com.example.reactivewebexample.category.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.example.reactivewebexample.category.dto.Categories;
import com.example.reactivewebexample.category.dto.CategoryComposite;
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
        ObjectId c1Oid = new ObjectId();
        ReflectionTestUtils.setField(c, "id", cOid);
        ReflectionTestUtils.setField(c1, "id", c1Oid);
        ReflectionTestUtils.setField(c2, "id", new ObjectId());
        ReflectionTestUtils.setField(c3, "id", new ObjectId());
        ReflectionTestUtils.setField(c4, "id", new ObjectId());
        ReflectionTestUtils.setField(c5, "id", new ObjectId());
        ReflectionTestUtils.setField(c6, "id", new ObjectId());
        ReflectionTestUtils.setField(c7, "id", new ObjectId());
        ReflectionTestUtils.setField(c8, "id", new ObjectId());
        Categories categories = new Categories(c);
        categories.addChildCategory(c1);
        categories.addChildCategory(c2);
        categories.addChildCategory(c3);
        categories.addChildCategory(c4);
        Categories categories2 = new Categories(c5);
        categories2.addChildCategory(c6);
        categories2.addChildCategory(c7);
        categories2.addChildCategory(c8);

        CategoryComposite categoryComposite = new CategoryComposite();

        ReflectionTestUtils.setField(categoryComposite, "categoryComposite",
            List.of(categories,categories2));

        given(service.retrieveCategories()).willReturn(Mono.just(categoryComposite));

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
            .jsonPath("$.content[0].category.name", "example").exists()
            .jsonPath("$.content[0].category.links[0].href", "/api/categories" + cOid.toHexString()).exists()
            .jsonPath("$.content[0].children[0].category.name", "example1").exists()
            .jsonPath("$.content[0].children[0].category.links[0].href", "/api/categories" + c1Oid.toHexString()).exists()
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
