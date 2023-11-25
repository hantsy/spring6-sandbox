package com.example.demo;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.common.Json;
import com.github.tomakehurst.wiremock.http.Body;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import wiremock.com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest()
@WireMockTest(httpPort = 8080)
@ActiveProfiles("test")
public class PostClientTest {

    @TestConfiguration
    @Import({
            ClientConfig.class,
            PostClient.class,
            JacksonObjectMapperConfig.class
    })
    @ImportAutoConfiguration(JacksonAutoConfiguration.class)
    static class TestConfig {
    }

    static {
        var wireMockObjectMapper = Json.getObjectMapper();
        wireMockObjectMapper.disable(wiremock.com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        wireMockObjectMapper.disable(wiremock.com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        wireMockObjectMapper.disable(wiremock.com.fasterxml.jackson.databind.DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        wireMockObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        var module = new wiremock.com.fasterxml.jackson.datatype.jsr310.JavaTimeModule();
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
                .withHeader("Accept", containing("application/json"))
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
                .withHeader("Content-Type", containing("application/json"))
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
                .withHeader("Content-Type", containing("application/json"))
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
                .withHeader("Accept", notContaining("application/json"))
        );
    }
}
