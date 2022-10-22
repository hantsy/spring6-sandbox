package com.example.demo.event.async;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AsyncGreetingPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishGreetingEvent(String message) {
        applicationEventPublisher.publishEvent(new AsyncGreeting(message));
    }
}
