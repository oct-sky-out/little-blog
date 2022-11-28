package com.example.reactivewebexample.comment.repository;

import com.example.reactivewebexample.comment.document.Comment;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CommentRepository extends ReactiveMongoRepository<Comment, ObjectId> {
    Flux<Comment> findAllByBoardId(ObjectId boardId);
}
