package com.example.demo.web;

import com.example.demo.domain.repository.CommentRepository;
import com.example.demo.domain.repository.PostRepository;
import com.example.demo.event.transactional.CommentCreatedEventPublisher;
import com.example.demo.event.transactional.PostCreatedEventPublisher;
import com.example.demo.service.PostService;
import com.example.demo.service.SseEventBroadcaster;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.concurrent.Executor;

import static org.mockito.Mockito.mock;

@Configuration
@Import(PostService.class)
class TestDataConfig {

    @Bean
    public PostRepository mockedPostRepository() {
        return mock(PostRepository.class);
    }

    @Bean
    public CommentRepository mockedCommentRepository() {
        return mock(CommentRepository.class);
    }

    @Bean
    public PostCreatedEventPublisher mockedPostEventPublisher() {
        return mock(PostCreatedEventPublisher.class);
    }

    @Bean
    public CommentCreatedEventPublisher mockedCommentCreatedEventPublisher() {
        return mock(CommentCreatedEventPublisher.class);
    }

    @Bean
    public SseEventBroadcaster mockedSseEventBroadcaster() {
        return mock(SseEventBroadcaster.class);
    }

    @Bean// will disable @Async on mock
    public Executor mockedExecutor() {
        return mock(Executor.class);
    }
}
