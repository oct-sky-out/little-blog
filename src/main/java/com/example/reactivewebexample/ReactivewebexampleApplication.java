package com.example.reactivewebexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@EnableReactiveMongoRepositories
@SpringBootApplication
public class ReactivewebexampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactivewebexampleApplication.class, args);
	}

}
