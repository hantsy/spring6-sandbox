package com.example.demo.event.transactional;

import com.example.demo.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentCreatedEventListener {
    private final PostRepository posts;
    private CopyOnWriteArraySet<CommentCreatedEvent> eventStore = new CopyOnWriteArraySet<>();

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void onCommentCreated(CommentCreatedEvent event) {
        log.debug("on post created: {}", event);
        this.eventStore.add(event);
    }


    @Scheduled(initialDelay = 3000L, fixedRate = 5000L)
    public void updateCommentsCount() {
        eventStore.forEach(e -> posts.increaseCommentsCount(e.eventData().getPostId().getId()));
        eventStore.clear();
    }
}
