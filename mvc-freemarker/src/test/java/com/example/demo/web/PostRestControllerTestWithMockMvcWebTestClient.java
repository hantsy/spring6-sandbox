package com.example.demo.web;

import com.example.demo.Jackson2ObjectMapperConfig;
import com.example.demo.domain.model.Post;
import com.example.demo.domain.repository.PostRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.Mockito.*;

/**
 * @author hantsy
 */
@SpringJUnitWebConfig(classes = {WebConfig.class, Jackson2ObjectMapperConfig.class, TestDataConfig.class})
public class PostRestControllerTestWithMockMvcWebTestClient {

    @Autowired
    WebApplicationContext ctx;

    WebTestClient rest;

    @Autowired
    PostRepository posts;

    @BeforeEach
    public void setup() {
        this.rest = MockMvcWebTestClient
                .bindToApplicationContext(ctx)
                .configureClient()
                .build();
    }

    @AfterEach
    public void teardown() {
        reset(this.posts);
    }

    @Test
    public void getGetAllPosts() throws Exception {
        when(this.posts.findAll())
                .thenReturn(List.of(
                                Post.of("test", "content of test1"),
                                Post.of("test2", "content of test2")
                        )
                );

        this.rest
                .get()
                .uri("/api/posts")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Post.class).hasSize(2);

        verify(this.posts, times(1)).findAll();
        verifyNoMoreInteractions(this.posts);
    }

}
