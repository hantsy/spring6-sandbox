package com.example.demo.web;

import com.example.demo.domain.repository.PostRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
class TestDataConfig {

    @Bean
    PostRepository mockedPostRepository() {
        return mock(PostRepository.class);
    }
}
