package com.example.reactivewebexample.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Getter
public class ModifyDto <T> {
    private String id;

    private T diff;

    public static <T2> Mono<ModifyDto<T2>> toMono(String id, T2 diff) {
        return Mono.just(new ModifyDto<>(id, diff));
    }
}
