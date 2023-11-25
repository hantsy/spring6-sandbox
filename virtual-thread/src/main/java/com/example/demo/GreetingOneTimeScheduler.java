package com.example.demo;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

@Slf4j
public class GreetingOneTimeScheduler {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Scheduled(initialDelay = 100)
    public void init() {
        log.debug("Scheduled to send message with initial delay...");
        applicationEventPublisher.publishEvent(new Greeting("Initial Delay ... at " + LocalDateTime.now()));
    }

}
