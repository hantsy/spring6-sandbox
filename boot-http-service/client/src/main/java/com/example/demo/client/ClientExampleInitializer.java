package com.example.demo.client;


import com.example.demo.shared.Post;
import com.example.demo.shared.PostApi;
import com.example.demo.shared.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
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
        client.allPosts().subscribe(
                data -> log.debug("The existing post: {}", data)
        );

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
                        }
                )
                .flatMap(post -> {
                    log.debug("getting post: {}", post);
                    return client.update(post.id(), new Post(null, "updated test", "updated content", Status.PENDING_MODERATION, null));
                })
                .subscribe(
                        responseEntity -> log.debug("updated status: {}", responseEntity)
                );
    }
}
