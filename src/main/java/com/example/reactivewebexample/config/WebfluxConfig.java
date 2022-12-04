package com.example.reactivewebexample.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurerComposite;

@Configuration
@EnableWebFlux
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class WebfluxConfig extends WebFluxConfigurerComposite {
}
