package com.example.demo.web;

import com.example.demo.domain.model.Post;
import com.example.demo.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;
import java.util.UUID;

import static org.springframework.web.servlet.function.ServerResponse.*;


/**
 * @author hantsy
 */
@Component
@RequiredArgsConstructor
public class PostHandler {

    private final PostRepository posts;

    public ServerResponse all(ServerRequest req) {
        return ok().body(this.posts.findAll());
    }

    @SneakyThrows
    public ServerResponse create(ServerRequest req) {
        var data = req.body(CreatePostCommand.class);
        var saved = this.posts.save(Post.builder().title(data.title()).content(data.content()).build());
        return created(URI.create("/posts/" + saved.getId())).build();
    }

    public ServerResponse get(ServerRequest req) {
        return this.posts.findById(UUID.fromString(req.pathVariable("id")))
                .map(p -> ok().body(p))
                .orElse(notFound().build());
    }

    @SneakyThrows
    public ServerResponse update(ServerRequest req) {
        var data = req.body(UpdatePostCommand.class);
        return this.posts.findById(UUID.fromString(req.pathVariable("id")))
                .map(p -> {
                    p.setTitle(data.title());
                    p.setContent(data.content());
                    p.setStatus(data.status());
                    this.posts.save(p);
                    return noContent().build();
                })
                .orElse(notFound().build());
    }

    public ServerResponse delete(ServerRequest req) {
            this.posts.deleteById(UUID.fromString(req.pathVariable("id")));
            return noContent().build();
    }

}