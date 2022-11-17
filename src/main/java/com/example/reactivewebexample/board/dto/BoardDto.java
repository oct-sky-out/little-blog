package com.example.reactivewebexample.board.dto;

import com.example.reactivewebexample.base.document.BaseField;
import com.example.reactivewebexample.board.document.Board;
import java.time.LocalDateTime;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;

@Builder
public record BoardDto(
    @Nullable
    String id,
    @NotEmpty
    String title,
    @NotEmpty
    String content,
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", iso = DateTimeFormat.ISO.DATE_TIME)
    @NotNull
    LocalDateTime createdAt,
    @Nullable
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime updatedAt) {

    public Board parseBoard() {
        BaseField baseField = BaseField.builder()
            .createdAt(LocalDateTime.now())
            .updatedAt(null)
            .isDeleted(0)
            .version(1)
            .build();

        return Board.builder()
            .title(this.title)
            .content(this.content)
            .baseField(baseField)
            .build();
    }
}
