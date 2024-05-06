package com.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Fallback;

@Configuration
public class Config {

    @Bean
    public Printer printer(Greeter greeter) {
        return new Printer(greeter);
    }

    @Bean
    @Fallback
    public Greeter fallbackGreeter() {
        return new FallbackGreeter();
    }
}
