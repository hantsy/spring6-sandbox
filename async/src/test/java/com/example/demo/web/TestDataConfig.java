package com.example.demo.web;

import com.example.demo.AsyncConfig;
import com.example.demo.domain.repository.PostRepository;
import com.example.demo.service.PostCreated;
import com.example.demo.service.PostEventPublisher;
import com.example.demo.service.SseEventBroadcaster;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.concurrent.Executor;

import static org.mockito.Mockito.mock;

@Configuration
class TestDataConfig {

    @Bean
    public PostRepository mockedRepository() {
        return mock(PostRepository.class);
    }

    @Bean
    public PostEventPublisher mockedPostEventPublisher() {
        return mock(PostEventPublisher.class);
    }

    @Bean
    public SseEventBroadcaster mockedSseEventBroadcaster() {
        return mock(SseEventBroadcaster.class);
    }

    @Bean// will disable mock on async
    public Executor mockedExecutor() {
        return mock(Executor.class);
    }
}
