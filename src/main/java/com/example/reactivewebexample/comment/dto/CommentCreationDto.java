package com.example.reactivewebexample.comment.dto;


import com.example.reactivewebexample.base.document.BaseField;
import com.example.reactivewebexample.comment.document.Comment;
import java.time.LocalDateTime;
import javax.validation.constraints.NotEmpty;

public record CommentCreationDto(@NotEmpty String password, @NotEmpty String name, @NotEmpty String content) {
    public static Comment parseComment(CommentCreationDto creationDto) {
        BaseField baseField = BaseField.builder()
            .createdAt(LocalDateTime.now())
            .isDeleted(0)
            .version(1)
            .build();

        return Comment.builder()
            .name(creationDto.name)
            .content(creationDto.content)
            .password(creationDto.password)
            .baseField(baseField)
            .build();
    }
}
