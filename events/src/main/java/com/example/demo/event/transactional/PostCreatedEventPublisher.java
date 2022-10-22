package com.example.demo.event.transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostCreatedEventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    public void publishPostCreated(PostCreated event) {
        log.debug("publishing event: {}", event);
        //this.eventPublisher.publishEvent(new PostCreatedEvent(this, event));
        this.eventPublisher.publishEvent(event);
    }
}
