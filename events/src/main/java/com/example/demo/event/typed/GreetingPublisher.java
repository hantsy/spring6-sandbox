package com.example.demo.event.typed;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GreetingPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishGreetingEvent(String message) {
        applicationEventPublisher.publishEvent(new Greeting(message));
    }
}
