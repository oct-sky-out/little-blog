package com.example.reactivewebexample.comment.controller;

import com.example.reactivewebexample.comment.dto.CommentCreationDto;
import com.example.reactivewebexample.comment.dto.CommentDto;
import com.example.reactivewebexample.comment.dto.UpdateCommentDto;
import com.example.reactivewebexample.comment.service.CommentService;
import com.example.reactivewebexample.common.dto.CreationDto;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/boards/{boardId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping
    public Flux<CommentDto> retrieveComments(@PathVariable String boardId) {
        return commentService.retrieveComments(boardId);
    }

    @PostMapping
    public Mono<CreationDto> createComment(@PathVariable String boardId,
                                           @RequestBody @Valid CommentCreationDto commentDto) {
        return commentService.addComment(boardId, commentDto);
    }

    @PutMapping("/{commentId}")
    public Mono<UpdateCommentDto> updateComment(@PathVariable String commentId,
                                                @RequestBody UpdateCommentDto updateCommentDto) {
        return commentService.updateComment(commentId, updateCommentDto);
    }
}
