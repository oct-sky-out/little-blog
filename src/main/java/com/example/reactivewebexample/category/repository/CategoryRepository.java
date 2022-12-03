package com.example.reactivewebexample.category.repository;

import com.example.reactivewebexample.category.document.Category;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CategoryRepository extends ReactiveMongoRepository<Category, ObjectId> {
}
