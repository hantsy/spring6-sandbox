package com.example.demo.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class HelloClient {

    private final WebClient client;

    public Mono<String> greet(String name) {
        return client.get().uri( uriBuilder -> uriBuilder.path("/greeting").queryParam("name", name).build())
                .exchangeToMono(response -> {
                    if(response.statusCode().equals(HttpStatus.OK)){
                        return response.bodyToMono(String.class);
                    }
                    return response.createError();
                });

    }
}
