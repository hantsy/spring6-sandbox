package com.example.demo.event.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AsyncGreetingListener {

    @EventListener
    @Async
    public void onGreetingEvent(AsyncGreeting event) {
        log.debug("received event:{}", event);
    }
}
