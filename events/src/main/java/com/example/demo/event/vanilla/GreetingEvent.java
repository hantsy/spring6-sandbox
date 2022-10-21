package com.example.demo.event.vanilla;

import org.springframework.context.ApplicationEvent;

public class GreetingEvent extends ApplicationEvent {
    private final String message;

    public GreetingEvent(Object source, String message) {
        super(source);
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}
