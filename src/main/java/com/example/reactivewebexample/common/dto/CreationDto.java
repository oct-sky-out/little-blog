package com.example.reactivewebexample.common.dto;


import reactor.core.publisher.Mono;

public record CreationDto(String id) {
    public static Mono<CreationDto> toMono(String id) {
        return Mono.just(new CreationDto(id));
    }
}
