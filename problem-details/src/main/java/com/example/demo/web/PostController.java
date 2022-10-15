package com.example.demo.web;

import com.example.demo.domain.exception.PostNotFoundException;
import com.example.demo.domain.model.Post;
import com.example.demo.domain.repository.PostRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.noContent;


@RestController
@RequestMapping("/posts")
@Validated
@RequiredArgsConstructor
public class PostController {
    private final PostRepository posts;

    @GetMapping
    public Flux<Post> all() {
        return this.posts.findAll();
    }

    @PostMapping
    public Mono<ResponseEntity<Object>> create(@RequestBody @Valid CreatePostCommand data) {
        return this.posts.save(Post.of(data.title(), data.content()))
                .map(id -> created(URI.create("/posts/" + id)).build());
    }

    @GetMapping("{id}")
    public Mono<ResponseEntity<Post>> get(@PathVariable UUID id) {
        return this.posts.findById(id)
                .switchIfEmpty(Mono.error(new PostNotFoundException(id)))
                .map(ResponseEntity::ok);
    }


    @PutMapping("{id}")
    public Mono<ResponseEntity<Object>> update(@PathVariable UUID id, @RequestBody @Valid UpdatePostCommand data) {
        return this.posts.findById(id)
                .switchIfEmpty(Mono.error(new PostNotFoundException(id)))
                .flatMap(post ->
                        this.posts.update(new Post(post.id(), data.title(), data.content(), post.status(), post.createdAt()))
                )
                .map(__ -> noContent().build());
    }

    @DeleteMapping("{id}")
    public Mono<ResponseEntity<Object>> delete(@PathVariable UUID id) {
        return this.posts.deleteById(id)
                .mapNotNull(deleted -> {
                    if (deleted > 0) {
                        return noContent().build();
                    }
                    throw new PostNotFoundException(id);
                });
    }
}
