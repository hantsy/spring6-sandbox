package com.example.demo.web;

import com.example.demo.Jackson2ObjectMapperConfig;
import com.example.demo.domain.model.Post;
import com.example.demo.domain.repository.PostRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * @author hantsy
 */
@SpringJUnitConfig(classes = {WebConfig.class, Jackson2ObjectMapperConfig.class, PostControllerTest.TestConfig.class})
@ActiveProfiles("mock")
public class PostControllerTest {

    @Autowired
    PostController postController;

    @Autowired
    RestExceptionHandler controllerAdvice;

    @Autowired
    PostRepository posts;

    WebTestClient client;

    @BeforeEach
    public void setup() {
        this.client = WebTestClient
                .bindToController(postController)
                .controllerAdvice(controllerAdvice)
                .configureClient()
                .build();
    }

    @AfterEach
    public void teardown() {
        reset(this.posts);
    }

    @Test
    public void testGetAllPosts() {
        given(this.posts.findAll())
                .willReturn(Flux.just(Post.of("test", "content")));
        this.client
                .get().uri("/posts").accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].title").isEqualTo("test");

        verify(this.posts, times(1)).findAll();
        verifyNoMoreInteractions(this.posts);
    }

    @Test
    public void getPost_whenNotFound() {
        given(this.posts.findById(any(UUID.class)))
                .willReturn(Mono.empty());
        var postId = UUID.randomUUID();

        this.client
                .get().uri("/posts/{id}", postId)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.id").isEqualTo(postId.toString())
                .jsonPath("$.entity").isEqualTo("POST");

        verify(this.posts, times(1)).findById(postId);
        verifyNoMoreInteractions(this.posts);
    }

    // Bean validation on Record is still not working
    @Test
    public void createPost_whenBodyInvalid() {
        var body = new CreatePostCommand("test", null);
        this.client
                .post().uri("/posts")
                .contentType(APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest();
    }


    @Configuration
    @ComponentScan(basePackageClasses = WebConfig.class)
    static class TestConfig {

        @Bean
        @Profile("mock")
        PostRepository mockedPostRepository() {
            return mock(PostRepository.class);
        }
    }

}
