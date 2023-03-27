package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class GreetingListener {

    @RabbitListener(queues = {DemoApplication.QUEUE_HELLO})
    public void onGreeting(Greeting greeting) {
        log.debug("received greeting: {} at {}", greeting, LocalDateTime.now());
    }
}
