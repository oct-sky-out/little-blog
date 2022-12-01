package com.example.reactivewebexample.likeit.repository;

import com.example.reactivewebexample.likeit.document.LikeIt;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface LikeItRepository extends ReactiveMongoRepository<LikeIt, ObjectId> {
}
