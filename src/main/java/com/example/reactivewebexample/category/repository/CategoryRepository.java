package com.example.reactivewebexample.category.repository;

import com.example.reactivewebexample.category.document.Category;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Mono;

public interface CategoryRepository extends ReactiveMongoRepository<Category, ObjectId> {
    @Query(value = "{parentId: ObjectId(?0)}", count = true)
    Mono<Long> countAllChildrenByParentId(@Param("parentId") String parentId);
}
