package com.example.demo.event.transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentCreatedEventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    public void publishCommentCreated(CommentCreatedEvent event) {
        log.debug("publishing event: {}", event);
        this.eventPublisher.publishEvent(event);
    }
}
