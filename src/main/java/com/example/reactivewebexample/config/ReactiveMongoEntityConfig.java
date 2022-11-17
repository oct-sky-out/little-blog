package com.example.reactivewebexample.config;

import com.example.reactivewebexample.base.document.BaseField;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Configuration
@EnableReactiveMongoAuditing
@EnableReactiveMongoRepositories(basePackages = "com.example.reactivewebexample.base.document")
public class ReactiveMongoEntityConfig {

    @Bean
    public ReactiveAuditorAware<BaseField> auditorProvider() {
        return () -> Mono.just(BaseField.builder()
            .createdAt(LocalDateTime.now())
            .updatedAt(null)
            .version(1)
            .isDeleted(0)
            .build());
    }


}
