package com.example.demo.service;

import org.springframework.context.ApplicationEvent;

public class PostCreatedEvent extends ApplicationEvent {
    private PostCreated event;

    public PostCreatedEvent(Object source, PostCreated event) {
        super(source);
        this.event = event;
    }
}
