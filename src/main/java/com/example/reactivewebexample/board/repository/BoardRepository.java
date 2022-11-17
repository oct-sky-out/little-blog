package com.example.reactivewebexample.board.repository;

import com.example.reactivewebexample.board.document.Board;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface BoardRepository extends ReactiveMongoRepository<Board, String> {
}
