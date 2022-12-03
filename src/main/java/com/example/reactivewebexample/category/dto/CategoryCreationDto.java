package com.example.reactivewebexample.category.dto;

import javax.validation.constraints.NotEmpty;

public record CategoryCreationDto(@NotEmpty String name) {
}
