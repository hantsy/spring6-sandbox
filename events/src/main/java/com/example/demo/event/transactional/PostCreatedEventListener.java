package com.example.demo.event.transactional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@Slf4j
public class PostCreatedEventListener {

    private CopyOnWriteArraySet<PostCreatedEvent> eventStore = new CopyOnWriteArraySet<>();

    //@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    @EventListener
    @Async
    public void onPostCreated(PostCreatedEvent post) {
        log.debug("on post created: {}", post);
        this.eventStore.add(post);
    }

    public Set<PostCreatedEvent> getEvents() {
        return this.eventStore;
    }
}
