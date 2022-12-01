package com.example.reactivewebexample.config;

import com.example.reactivewebexample.base.document.BaseField;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Configuration
@EnableReactiveMongoAuditing
@EnableReactiveMongoRepositories(basePackages = "com.example.reactivewebexample")
public class ReactiveMongoEntityConfig extends AbstractReactiveMongoConfiguration {
    @Value("${mongodb.url}")
    private String url;

    @Bean
    public ReactiveAuditorAware<BaseField> auditorProvider() {
        return () -> Mono.just(BaseField.builder()
            .createdAt(LocalDateTime.now())
            .updatedAt(null)
            .version(1)
            .isDeleted(0)
            .build());
    }

    @Override
    protected String getDatabaseName() {
        return "my-blog";
    }

    @Override
    public MongoClient reactiveMongoClient() {
        return MongoClients.create(url);
    }
}
