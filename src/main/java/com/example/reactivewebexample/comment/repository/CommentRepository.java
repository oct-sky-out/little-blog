package com.example.reactivewebexample.comment.repository;

import com.example.reactivewebexample.comment.document.Comment;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface CommentRepository extends ReactiveMongoRepository<Comment, String> {
    Flux<Comment> findAllByBoardId(String boardId);
}
