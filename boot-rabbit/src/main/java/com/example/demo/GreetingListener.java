package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class GreetingListener {

    public List<String> messages = new ArrayList<>();

    @RabbitListener(queues = {DemoApplication.QUEUE_HELLO})
    public void onGreeting(Greeting greeting) {
        log.debug("received greeting: {} at {}", greeting, LocalDateTime.now());
        messages.add(greeting.message());
    }
}
