package com.example.demo.web;

import com.example.demo.Jackson2ObjectMapperConfig;
import com.example.demo.domain.model.Post;
import com.example.demo.domain.repository.PostRepository;
import com.example.demo.event.transactional.PostCreatedEvent;
import com.example.demo.event.transactional.PostCreatedEventPublisher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

/**
 * @author hantsy
 */
@SpringJUnitWebConfig(classes = {Jackson2ObjectMapperConfig.class, WebConfig.class, TestDataConfig.class})
@ActiveProfiles("test")
public class PostControllerTestWithMockMvcWebTestClient {

    @Autowired
    PostController ctrl;

    WebTestClient client;

    @Autowired
    PostRepository posts;

    @Autowired
    PostCreatedEventPublisher eventPublisher;

    @BeforeEach
    public void setup() {
        this.client = MockMvcWebTestClient
                .bindToController(ctrl)
                .configureClient()
                .build();
    }

    @AfterEach
    public void teardown() {
        reset(this.posts, this.eventPublisher);
    }

    @Test
    public void getAllPostsWillBeOk() throws Exception {
        when(this.posts.readAllBy())
                .thenReturn(CompletableFuture.completedFuture(
                                List.of(
                                        Post.builder().title("test").content("content of test1").build(),
                                        Post.builder().title("test2").content("content of test2").build()
                                )
                        )
                );

        this.client.get().uri("/posts")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.size()").isEqualTo(2);

        verify(this.posts, times(1)).readAllBy();
        verifyNoMoreInteractions(this.posts);
    }

    @Test
    public void testCreatePost() throws Exception {
        var id = UUID.randomUUID();
        when(this.posts.save(any(Post.class)))
                .thenReturn(Post.builder().id(id).title("test").content("content of test").build());
        doNothing().when(this.eventPublisher).publishPostCreated(any(PostCreatedEvent.class));

        var data = new CreatePostCommand("test post", "content of test");
        this.client.post().uri("/posts").body(BodyInserters.fromValue(data))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists("Location")
                .expectHeader().location("/posts/" + id);

        verify(this.posts, times(1)).save(any(Post.class));
        verify(this.eventPublisher, times(1)).publishPostCreated(any(PostCreatedEvent.class));
        verifyNoMoreInteractions(this.posts);
        verifyNoMoreInteractions(this.eventPublisher);
    }
}
