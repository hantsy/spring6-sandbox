package com.example.demo.web;


import com.example.demo.domain.model.Post;
import com.example.demo.domain.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;

import java.util.List;

import static org.mockito.Mockito.*;

/**
 * @author hantsy
 */
@WebMvcTest
public class PostControllerTestWithMockMvcWebTestClient {

    @Autowired
    PostController ctrl;

    WebTestClient rest;

    @MockBean
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
        when(this.posts.findAll(isA(Specification.class), isA(Pageable.class)))
                .thenReturn(new PageImpl<Post>(
                                List.of(
                                        Post.builder().title("test").content("data of test1").build(),
                                        Post.builder().title("test2").content("data of test2").build()
                                )
                        )
                );

        this.rest
                .get()
                .uri("/posts")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.count").isEqualTo(2);

        verify(this.posts, times(1)).findAll(isA(Specification.class), isA(Pageable.class));
        verifyNoMoreInteractions(this.posts);
    }


}
