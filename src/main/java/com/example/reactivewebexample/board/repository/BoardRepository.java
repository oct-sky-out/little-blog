package com.example.reactivewebexample.board.repository;

import com.example.reactivewebexample.board.document.Board;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends ReactiveMongoRepository<Board, ObjectId> {
}
