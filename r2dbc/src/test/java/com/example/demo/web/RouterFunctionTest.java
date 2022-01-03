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
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * @author hantsy
 */
@SpringJUnitConfig(classes = {WebConfig.class, Jackson2ObjectMapperConfig.class, RouterFunctionTest.TestConfig.class})
@ActiveProfiles("mock")
public class RouterFunctionTest {

    @Autowired
    RouterFunction<ServerResponse> routerFunction;

    @Autowired
    PostRepository posts;

    WebTestClient client;

    @BeforeEach
    public void setup() {
        this.client = WebTestClient
                .bindToRouterFunction(routerFunction)
                .configureClient()
                .build();
    }

    @AfterEach
    public void teardown() {
        reset(this.posts);
    }

    @Test
    public void testGetAllPosts() throws Exception {
        given(this.posts.findAll())
                .willReturn(Flux.just(Post.builder().title("test").content("content").build()));
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


    @Configuration
    @ComponentScan
    static class TestConfig {

        @Bean
        @Profile("mock")
        PostRepository mockedPostRepository() {
            return mock(PostRepository.class);
        }
    }

}
