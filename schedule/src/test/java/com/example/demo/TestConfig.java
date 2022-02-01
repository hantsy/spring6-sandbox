package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.spy;

@Configuration
@ComponentScan(basePackageClasses = {ScheduleConfig.class})
class TestConfig {

    @Bean
    @Primary
    Counter counterSpy(Counter counter){
        return spy(counter);
    }
}
