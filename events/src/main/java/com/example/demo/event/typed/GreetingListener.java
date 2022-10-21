package com.example.demo.event.typed;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GreetingListener {

    @EventListener
    public void onGreetingEvent(Greeting event) {
        log.debug("received event:{}", event);
    }
}
