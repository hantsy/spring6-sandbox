package com.example.demo.web;

import com.example.demo.Jackson2ObjectMapperConfig;
import com.example.demo.ValidationConfig;
import com.example.demo.domain.repository.PostRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.mockito.Mockito.mock;

@Configuration
class TestDataConfig {

    @Bean
    public PostRepository mockedRepository() {
        return mock(PostRepository.class);
    }
}
