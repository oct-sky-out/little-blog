package com.example.reactivewebexample.board.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record BoardDto(String id, String title, String content, LocalDateTime createdAt,
                       LocalDateTime updatedAt) {
}
