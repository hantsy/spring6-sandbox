package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class Greeting {

    public void sayHello() {
        log.debug("Hello at {}:", LocalDateTime.now());
    }
}
