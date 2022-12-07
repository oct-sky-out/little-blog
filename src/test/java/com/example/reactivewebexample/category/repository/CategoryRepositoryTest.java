package com.example.reactivewebexample.category.repository;

import com.example.reactivewebexample.category.document.Category;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@Slf4j
@DataMongoTest
class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository repository;

    List<Category> categories;
    ObjectId parentId;

    @BeforeEach
    void setUp() {
        Category category1 = new Category("1");
        Category category2 = new Category("2");
        Category category3 = new Category("3");
        parentId = new ObjectId();

        ReflectionTestUtils.setField(category1, "id", parentId);
        ReflectionTestUtils.setField(category2, "id", new ObjectId());
        ReflectionTestUtils.setField(category2, "parentId", parentId);
        ReflectionTestUtils.setField(category3, "id", new ObjectId());

        categories = List.of(category1, category2, category3);
        Flux.fromIterable(List.of(category1, category2, category3))
            .flatMap(repository::save)
            .subscribe(category -> log.info("generated document! : " +
                category.getId().toHexString()));
    }

    @Test
    void findAllChildrenByParentId() {
        StepVerifier.create(repository.countAllChildrenByParentId(parentId.toString()).log())
            .expectSubscription()
            .expectNextCount(1)
            .verifyComplete();
    }

}
