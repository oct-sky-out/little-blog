
package com.example.reactivewebexample.likeit.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class LikeItAdapter {
    private final WebClient webClient;
}
