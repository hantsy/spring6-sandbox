package com.example.demo;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;

@Slf4j
public class GreetingListener {


    @EventListener
    public void onGreetingEvent(Greeting greeting) {
        log.debug("Received: {}", greeting);
    }
}
