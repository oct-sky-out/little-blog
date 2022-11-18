package com.example.reactivewebexample.comment.service;

import com.example.reactivewebexample.comment.dto.CommentCreationDto;
import com.example.reactivewebexample.comment.dto.CommentDto;
import com.example.reactivewebexample.common.dto.CreationDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommentService {
    Flux<CommentDto> retrieveComments(String boardId);

    Mono<CreationDto> addComment(String boardId, CommentCreationDto commentDto);
}
