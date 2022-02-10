package com.example.demo.domain.repository;

import com.example.demo.domain.model.Post;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PostRepository {
    Flux<Post> findByTitleContains(String name);

    Flux<Post> findAll();

    // see: https://stackoverflow.com/questions/64267699/spring-data-r2dbc-and-group-by
    Flux<Map<Object, Object>> countByStatus();

    Mono<Post> findById(UUID id);

    Mono<UUID> save(Post p);

    Flux<UUID> saveAll(List<Post> data);

    Mono<Long> update(Post p);

    Mono<Long> deleteById(UUID id);

    Mono<Long> deleteAll();
}
