package com.example.demo.web;

import com.example.demo.domain.model.CreatePostCommand;
import com.example.demo.domain.model.Post;
import com.example.demo.domain.model.UpdatePostCommand;
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
        var id = this.posts.save(Post.of(data.title(), data.content()));
        return created(URI.create("/posts/" + id)).build();
    }

    public ServerResponse get(ServerRequest req) {
        var data = this.posts.findById(UUID.fromString(req.pathVariable("id")));
        if (data != null) {
            return ok().body(data);
        } else {
            return notFound().build();
        }
    }

    @SneakyThrows
    public ServerResponse update(ServerRequest req) {
        var data = req.body(UpdatePostCommand.class);
        var id = UUID.fromString(req.pathVariable("id"));
        var existed = this.posts.findById(id);
        if (existed == null) return notFound().build();

        var updated = this.posts.update(
                new Post(existed.id(),
                        data.title(),
                        data.content(),
                        data.status(),
                        existed.createdAt()
                )
        );
        if (updated > 0) {
            return noContent().build();
        } else {
            return notFound().build();
        }
    }

    public ServerResponse delete(ServerRequest req) {
        var deleted = this.posts.deleteById(UUID.fromString(req.pathVariable("id")));
        if (deleted > 0) {
            return noContent().build();
        } else {
            return notFound().build();
        }
    }

}