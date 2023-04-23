package com.example.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
@RequiredArgsConstructor
class ProductHandler {
    final ProductRepository productRepository;

    public Mono<ServerResponse> getAll(ServerRequest request) {
        var data = productRepository.findAll();
        return ok().body(BodyInserters.fromPublisher(data, new ParameterizedTypeReference<Product>() {
        }));
    }

}
