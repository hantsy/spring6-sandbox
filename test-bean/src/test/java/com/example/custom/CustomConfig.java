package com.example.custom;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.bean.override.BeanOverrideProcessor;

@Configuration
public class CustomConfig {

    @Bean
    public BeanOverrideProcessor stubBeanOverrideProcessor() {
        return new StubBeanOverrideProcessor();
    }
}
