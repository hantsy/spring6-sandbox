package com.example.demo.web;

import com.example.demo.Jackson2ObjectMapperConfig;
import com.example.demo.domain.model.Post;
import com.example.demo.domain.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.function.ServerRequest;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.servlet.function.ServerResponse.ok;

/**
 * @author hantsy
 */
@SpringJUnitConfig(classes = {WebConfig.class, Jackson2ObjectMapperConfig.class, RouterFunctionTest.TestConfig.class})
@ExtendWith(MockitoExtension.class)
public class RouterFunctionTest {

    @Autowired
    WebApplicationContext ctx;

    WebTestClient client;

    @Autowired
    PostRepository posts;

    @BeforeEach
    public void setup() {
        this.client = MockMvcWebTestClient.bindToApplicationContext(ctx)
                .configureClient()
                .build();
    }

    @Test
    public void getGetAllPosts() throws Exception {
        when(this.posts.findByKeyword(anyString(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(
                                new Post("test", "content of test1"),
                                new Post("test2", "content of test2")
                        )
                );

        this.rest
                .get()
                .uri("/posts")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Post.class).hasSize(2);

        verify(this.posts, times(1)).findByKeyword("", null, 0, 10);
        verifyNoMoreInteractions(this.posts);
    }


    @Configuration
    static class TestConfig {

        @Bean
        PostRepository mockedPostRepository() {
          return mock(PostRepository.class);
        }
    }

}
