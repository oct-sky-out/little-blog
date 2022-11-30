package com.example.reactivewebexample.comment.dto;


import com.example.reactivewebexample.base.document.BaseField;
import com.example.reactivewebexample.base.util.BaseFieldFactory;
import com.example.reactivewebexample.comment.document.Comment;
import javax.validation.constraints.NotEmpty;
import reactor.core.publisher.Mono;

public record CommentCreationDto(@NotEmpty String password, @NotEmpty String name, @NotEmpty String content) {
    public static Mono<Comment> parseComment(CommentCreationDto creationDto) {
        BaseField baseField = BaseFieldFactory.create();

        return Mono.just(Comment.builder()
            .name(creationDto.name)
            .content(creationDto.content)
            .baseField(baseField)
            .build());
    }
}
