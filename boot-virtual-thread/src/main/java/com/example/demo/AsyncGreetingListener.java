package com.example.demo;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

@Slf4j
public class AsyncGreetingListener {


    @EventListener
    @Async
    public void onGreetingEvent(Greeting greeting) {
        log.debug("Received(async): {}", greeting);
    }
}
