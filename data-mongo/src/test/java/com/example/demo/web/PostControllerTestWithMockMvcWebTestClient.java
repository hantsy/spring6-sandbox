package com.example.demo.web;


import com.example.demo.Jackson2ObjectMapperConfig;
import com.example.demo.domain.model.Post;
import com.example.demo.domain.repository.PostRepository;
import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;

import java.util.List;

import static org.mockito.Mockito.*;

/**
 * @author hantsy
 */
@SpringJUnitWebConfig(classes = {Jackson2ObjectMapperConfig.class, WebConfig.class, TestDataConfig.class})
@ActiveProfiles("test")
public class PostControllerTestWithMockMvcWebTestClient {

    @Autowired
    PostController ctrl;

    WebTestClient rest;

    @Autowired
    PostRepository posts;

    @BeforeEach
    public void setup() {
        this.rest = MockMvcWebTestClient
                .bindToController(ctrl)
                .configureClient()
                .build();
    }

    @Test
    public void getAllPostsWillBeOk() throws Exception {
        when(this.posts.findAll(isA(Predicate.class), isA(Pageable.class)))
                .thenReturn(new PageImpl<Post>(
                                List.of(
                                        Post.builder().title("test").content("content of test1").build(),
                                        Post.builder().title("test2").content("content of test2").build()
                                )
                        )
                );

        this.rest
                .get()
                .uri("/posts")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.count").isEqualTo(2);

        verify(this.posts, times(1)).findAll(isA(Predicate.class), isA(Pageable.class));
        verifyNoMoreInteractions(this.posts);
    }


}
