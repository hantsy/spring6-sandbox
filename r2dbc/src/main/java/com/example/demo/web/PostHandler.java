package com.example.demo.web;

import com.example.demo.domain.model.Post;
import com.example.demo.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;

import static org.springframework.web.reactive.function.server.ServerResponse.*;


/**
 * @author hantsy
 */
@Component
@RequiredArgsConstructor
public class PostHandler {

    private final PostRepository posts;

    public Mono<ServerResponse> all(ServerRequest req) {
        return ok().body(this.posts.findAll(), Post.class);
    }

    public Mono<ServerResponse> create(ServerRequest req) {
        return req.bodyToMono(Post.class)
                .flatMap(this.posts::save)
                .flatMap(id -> created(URI.create("/posts/" + id)).build());
    }

    public Mono<ServerResponse> get(ServerRequest req) {
        return this.posts.findById(UUID.fromString(req.pathVariable("id")))
                .flatMap(post -> ok().body(Mono.just(post), Post.class))
                .switchIfEmpty(notFound().build());
    }

    public Mono<ServerResponse> update(ServerRequest req) {
        var existed = this.posts.findById(UUID.fromString(req.pathVariable("id")));
        return Mono
                .zip(
                        (data) -> {
                            Post p = (Post) data[0];
                            Post p2 = (Post) data[1];
                            return new Post(
                                    p.id(),
                                    p2 != null && StringUtils.hasText(p2.title()) ? p2.title() : p.title(),
                                    p2 != null && StringUtils.hasText(p2.content()) ? p2.content() : p.content(),
                                    p2 != null && p2.status() != null ? p2.status() : p.status(),
                                    p.createdAt()
                            );
                        },
                        existed,
                        req.bodyToMono(Post.class)
                )
                .cast(Post.class)
                .flatMap(this.posts::update)
                .flatMap(post -> noContent().build());
    }

    public Mono<ServerResponse> delete(ServerRequest req) {
        return this.posts.deleteById(UUID.fromString(req.pathVariable("id")))
                .flatMap(deleted -> noContent().build());
    }

}