package com.example.demo.shared;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@HttpExchange(url = "/posts", accept = "application/json", contentType = "application/json")
public interface PostApi {
    @GetExchange("")
    Flux<Post> allPosts();

    @GetExchange("/{id}")
    Mono<Post> getById(@PathVariable("id") UUID id);

    @PostExchange("")
    Mono<ResponseEntity<Void>> save(@RequestBody Post post);

    @PutExchange("/{id}")
    Mono<ResponseEntity<Void>> update(@PathVariable UUID id, @RequestBody Post post);

    @DeleteExchange("/{id}")
    Mono<ResponseEntity<Void>> delete(@PathVariable UUID id);
}
