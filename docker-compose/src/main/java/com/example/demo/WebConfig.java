package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;

@Configuration
class WebConfig {

    @Bean
    RouterFunction routerFunction(final ProductHandler handler) {
        return RouterFunctions.route()
            .GET("/products", handler::getAll)
            .build();
    }
}
