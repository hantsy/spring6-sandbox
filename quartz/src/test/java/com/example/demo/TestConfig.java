package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.spy;

@Configuration(proxyBeanMethods = false)
@ComponentScan(basePackageClasses = {QuartzConfig.class})
class TestConfig {

    @Bean
    @Primary
    Greeting greetingSpy(Greeting greeting){
        return spy(greeting);
    }
}