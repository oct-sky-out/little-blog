package com.example.reactivewebexample.board.dto;

import javax.validation.constraints.NotEmpty;

public record UpdateBoardDto(@NotEmpty String title, @NotEmpty String content) {
}
