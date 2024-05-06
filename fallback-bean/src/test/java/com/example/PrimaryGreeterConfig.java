package com.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class PrimaryGreeterConfig {

    @Bean
    @Primary
    public Greeter primaryGreeter() {
        return new PrimayGreeter();
    }
}
