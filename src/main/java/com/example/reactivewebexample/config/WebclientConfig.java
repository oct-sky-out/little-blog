package com.example.reactivewebexample.config;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebclientConfig {
    @Value("${user.baseurl}")
    private String baseurl;

    @Value("${user.port}")
    private int port;

    @Bean
    public WebClient webClient() {
        int timeoutLimit = 10;

        HttpClient httpClient = HttpClient.create()
            .responseTimeout(Duration.of(timeoutLimit, ChronoUnit.SECONDS))
            .baseUrl(baseurl)
            .port(port);

        return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();
    }
}
