package com.example.reactivewebexample.category.dto;

import javax.validation.constraints.NotEmpty;

public record CategorySaveDto(@NotEmpty String name) {
}
