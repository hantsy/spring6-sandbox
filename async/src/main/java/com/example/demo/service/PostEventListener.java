package com.example.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PostEventListener implements ApplicationListener<PostCreatedEvent> {

    @Override
    public void onApplicationEvent(PostCreatedEvent event) {
        log.debug("on post created: {}", event);
    }
}
