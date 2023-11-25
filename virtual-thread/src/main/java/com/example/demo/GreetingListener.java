package com.example.demo;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class GreetingListener {

    private final AtomicLong count= new AtomicLong(0L);
    @EventListener
    public void onGreetingEvent(Greeting greeting) {
        log.debug("Received: {}", greeting);
        var cnt =count.incrementAndGet();
        log.debug("The event listener is called: {}", cnt);
    }

    public long getInvocationCount() {
        return this.count.get();
    }
}
