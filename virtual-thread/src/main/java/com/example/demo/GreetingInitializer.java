package com.example.demo;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

@Slf4j
public class GreetingInitializer {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        log.debug("starting data initialization...");
        IntStream.range(1, 10).forEachOrdered(i -> {
            Greeting greeting = new Greeting("Message #" + i + " ... at " + LocalDateTime.now());
            log.debug("Sending: {}", greeting);
            applicationEventPublisher.publishEvent(greeting);
        });

    }

}
