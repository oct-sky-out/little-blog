package com.example.reactivewebexample.likeit.dto;

import com.example.reactivewebexample.base.document.BaseField;

public record LikeUserDto(String id, String githubId, String email, BaseField baseField) {
}
