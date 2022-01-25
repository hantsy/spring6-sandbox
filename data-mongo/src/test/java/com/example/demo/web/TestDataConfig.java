package com.example.demo.web;

import com.example.demo.domain.repository.PostRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import static org.mockito.Mockito.mock;

@Configuration
class TestDataConfig {

    @Bean
    public PostRepository mockedRepository() {
        return mock(PostRepository.class);
    }

    @Bean
    public GridFsTemplate gridFsTemplate() {
        return mock(GridFsTemplate.class);
    }
}
