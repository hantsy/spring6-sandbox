package com.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DummyConfig {

    @Bean
    public Greeter dummyGreeter() {
        return new DummyGreeter();
    }
}

