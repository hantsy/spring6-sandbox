package com.example.demo.server;

import com.example.demo.shared.Post;
import com.example.demo.shared.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

@Component
public class SampleDataInitializer {
    private static final Logger log = LoggerFactory.getLogger(SampleDataInitializer.class);

    private final PostRepository posts;

    public SampleDataInitializer(PostRepository posts) {
        this.posts = posts;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        Flux.just("First", "Second")
                .map(it -> new Post(null, it + "Post", "Content of " + it + " Post", Status.DRAFT, LocalDateTime.now()))
                .flatMap(posts::save)
                .subscribe(
                        data -> log.debug("saved post: {}", data),
                        error -> log.error("error: {}", error),
                        () -> log.info("saved post successfully")
                );
    }
}
