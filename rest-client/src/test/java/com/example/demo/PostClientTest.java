package com.example.demo;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.common.Json;
import com.github.tomakehurst.wiremock.http.Body;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringJUnitConfig(
        classes = {
                ClientConfig.class,
                Jackson2ObjectMapperConfig.class,
                PostClient.class
        }
)
@WireMockTest(httpPort = 8080)
public class PostClientTest {

    static {
        ObjectMapper wireMockObjectMapper = Json.getObjectMapper();
        wireMockObjectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        wireMockObjectMapper.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        wireMockObjectMapper.disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);

        JavaTimeModule module = new JavaTimeModule();
        wireMockObjectMapper.registerModule(module);
    }

    @Autowired
    PostClient postClient;

    @Autowired
    ObjectMapper objectMapper;

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
                .willReturn(
                        aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withResponseBody(Body.fromJsonBytes(Json.toByteArray(data)))
                )
        );

        var posts = postClient.allPosts();
        assertThat(posts.size()).isEqualTo(2);

        verify(getRequestedFor(urlEqualTo("/posts"))
                .withHeader("Accept", equalTo("application/json")));
    }

    @SneakyThrows
    @Test
    public void testGetPostById() {
        var id = UUID.randomUUID();
        var data = new Post(id, "title1", "content1", Status.DRAFT, LocalDateTime.now());

        stubFor(get("/posts/" + id)
                .willReturn(
                        aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withResponseBody(Body.fromJsonBytes(Json.toByteArray(data)))
                )
        );

        var post = postClient.getById(id);
        assertThat(post.id()).isEqualTo(id);
        assertThat(post.title()).isEqualTo(data.title());
        assertThat(post.content()).isEqualTo(data.content());
        assertThat(post.status()).isEqualTo(data.status());
        assertThat(post.createdAt()).isEqualTo(data.createdAt());


        verify(getRequestedFor(urlEqualTo("/posts/" + id))
                .withHeader("Accept", equalTo("application/json"))
        );
    }

    @Test
    public void testGetPostById_NotFound() {
        var id = UUID.randomUUID();
        stubFor(get("/posts/" + id)
                .willReturn(
                        aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withStatus(404)
                                .withResponseBody(Body.none())
                )
        );

        assertThatThrownBy(() -> postClient.getById(id)).isInstanceOf(PostNotFoundException.class);

        verify(getRequestedFor(urlEqualTo("/posts/" + id))
                .withHeader("Accept", equalTo("application/json"))
        );
    }

    @SneakyThrows
    @Test
    public void testCreatePost() {
        var id = UUID.randomUUID();
        var data = new Post(null, "title1", "content1", Status.DRAFT, null);

        stubFor(post("/posts")
                .willReturn(
                        aResponse()
                                .withHeader("Location", "/posts/" + id)
                                .withStatus(201)
                )
        );

        postClient.save(data);

        verify(postRequestedFor(urlEqualTo("/posts"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(Json.write(data)))
        );
    }

    @SneakyThrows
    @Test
    public void testUpdatePost() {
        var id = UUID.randomUUID();
        var data = new Post(null, "title1", "content1", Status.DRAFT, null);

        stubFor(put("/posts/" + id)
                .willReturn(
                        aResponse()
                                .withStatus(204)
                )
        );

        postClient.update(id, data);

        verify(putRequestedFor(urlEqualTo("/posts/" + id))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(Json.write(data)))
        );
    }

    @SneakyThrows
    @Test
    public void testDeletePostById() {
        var id = UUID.randomUUID();
        stubFor(delete("/posts/" + id)
                .willReturn(
                        aResponse()
                                .withStatus(204)
                )
        );

        postClient.delete(id);

        verify(deleteRequestedFor(urlEqualTo("/posts/" + id))
                .withHeader("Accept", equalTo("application/json"))
        );
    }
}
