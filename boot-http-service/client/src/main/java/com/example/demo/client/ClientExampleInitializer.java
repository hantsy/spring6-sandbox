package com.example.demo.client;

import com.example.demo.shared.Post;
import com.example.demo.shared.PostApi;
import com.example.demo.shared.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ClientExampleInitializer {
    private static final Logger log = LoggerFactory.getLogger(ClientExampleInitializer.class);
    private final PostApi client;

    public ClientExampleInitializer(PostApi client) {
        this.client = client;
    }

    @EventListener(ContextRefreshedEvent.class)
    void postClientExample() {

        log.debug("get all posts.");
        client.allPosts()
                .subscribe(
                        data -> log.debug("The existing post: {}", data)
                );

        log.debug("save post and update post");
        client.save(new Post(null, "test", "test content", Status.DRAFT, null))
                .log()
                .flatMap(saved -> {
                    var uri = saved.getHeaders().getLocation().toString();
                    var idString = uri.substring(uri.lastIndexOf("/") + 1);
                    log.debug("Post id: {}", idString);
                    return client.getById(UUID.fromString(idString))
                            .log()
                            .map(post -> {
                                log.debug("post: {}", post);
                                return post;
                            });
                })
                .flatMap(post -> {
                    log.debug("getting post: {}", post);
                    return client.update(post.id(), new Post(null, "updated test", "updated content", Status.PENDING_MODERATION, null));
                })
                .subscribe(
                        responseEntity -> log.debug("updated status: {}", responseEntity)
                );

        log.debug("get post by id that not existed.");
        client.getById(UUID.randomUUID())
                .subscribe(
                        post -> log.debug("post: {}", post),
                        error -> log.error("error:", error)
                );
    }
}
