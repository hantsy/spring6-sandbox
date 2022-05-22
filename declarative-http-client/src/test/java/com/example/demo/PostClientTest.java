package com.example.demo;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.http.Body;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringJUnitConfig(classes = {ClientConfig.class, Jackson2ObjectMapperConfig.class})
@WireMockTest(httpPort = 8080)
public class PostClientTest {

    @Autowired
    ObjectMapper objectMapper;


    @Autowired
    PostClient postClient;

    @SneakyThrows
    @BeforeEach
    public void setup() {

    }

    @SneakyThrows
    @Test
    public void testGetAllPosts() {
        var data = List.of(
                new Post(UUID.randomUUID(), "title1", "content1", Status.DRAFT, LocalDateTime.now()),
                new Post(UUID.randomUUID(), "title2", "content2", Status.PUBLISHED, LocalDateTime.now())
        );
        stubFor(get("/posts")
                .withHeader("Accept", equalTo("application/json"))
                .willReturn(
                        aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withResponseBody(Body.fromJsonBytes(objectMapper.writeValueAsBytes(data)))
                )
        );

        postClient.allPosts()
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }

    @SneakyThrows
    @Test
    public void testGetPostById() {
        var id = UUID.randomUUID();
        var data = new Post(id, "title1", "content1", Status.DRAFT, LocalDateTime.now());

        stubFor(get("/posts/" + id)
                .withHeader("Accept", equalTo("application/json"))
                .willReturn(
                        aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withResponseBody(Body.fromJsonBytes(objectMapper.writeValueAsBytes(data)))
                )
        );

        postClient.getById(id)
                .as(StepVerifier::create)
                .consumeNextWith(
                        post -> {
                            assertThat(post.id()).isEqualTo(id);
                            assertThat(post.title()).isEqualTo(data.title());
                            assertThat(post.content()).isEqualTo(data.content());
                            assertThat(post.status()).isEqualTo(data.status());
                            assertThat(post.createdAt()).isEqualTo(data.createdAt());
                        }
                )
                .verifyComplete();
    }

    @SneakyThrows
    @Test
    public void testCreatePost() {
        var id = UUID.randomUUID();
        var data = new Post(null, "title1", "content1", Status.DRAFT, null);

        stubFor(post("/posts")
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(data)))
                .willReturn(
                        aResponse()
                                .withHeader("Location", "/posts/" + id)
                                .withStatus(201)
                )
        );

        postClient.save(data)
                .as(StepVerifier::create)
                .consumeNextWith(
                        entity -> {
                            assertThat(entity.getHeaders().getLocation()).isEqualTo("/posts/" + id);
                            assertThat(entity.getStatusCode().value()).isEqualTo(201);
                        }
                )
                .verifyComplete();
    }
}
