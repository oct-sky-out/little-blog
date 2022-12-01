package com.example.reactivewebexample.likeit.service;


import org.bson.types.ObjectId;
import reactor.core.publisher.Mono;

public interface LikeItService {
    Mono<Void> initializeBoardLikeIt(ObjectId boardId);
}
