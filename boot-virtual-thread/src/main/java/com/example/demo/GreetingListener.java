package com.example.demo;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GreetingListener {


    @EventListener
    @Async
    public void onGreetingEvent(Greeting greeting) {
        log.debug("Received: {}", greeting);
    }
}
