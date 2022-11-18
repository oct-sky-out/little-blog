package com.example.reactivewebexample.comment.dto;

import com.example.reactivewebexample.comment.document.Comment;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import javax.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Flux;

@Builder
@Getter
public class CommentDto{
    @Nullable
    private final String id;
    @NotEmpty
    private final String name;
    @NotEmpty
    private final String content;
    @Nullable
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime createdAt;
    @Nullable
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime updatedAt;

    public static Flux<CommentDto> convert(Comment comment) {
        return Flux.just(CommentDto.builder()
                .id(comment.getId())
                .name(comment.getName())
                .content(comment.getContent())
                .createdAt(comment.getBaseField().getCreatedAt())
                .updatedAt(comment.getBaseField().getUpdatedAt())
                .build());
    }
}
