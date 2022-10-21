package com.example.demo.event.vanilla;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GreetingEventListener implements ApplicationListener<GreetingEvent> {
    @Override
    public void onApplicationEvent(GreetingEvent event) {
        log.debug("received events: {}, message: {}", event, event.getMessage());
    }
}
