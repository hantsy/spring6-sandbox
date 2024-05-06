package com.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@Slf4j
public class SampleListener {

    @EventListener
    public void init(ContextRefreshedEvent event){
        log.debug("listening context refreshed event via thread:{} ", Thread.currentThread().getName());
    }
}
