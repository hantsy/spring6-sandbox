package com.example.demo.it;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class IntegrationTests {

    WebTestClient client;

    @BeforeEach
    public void setup() {
        this.client = WebTestClient
                .bindToServer()
                .responseTimeout(Duration.ofMillis(5000))
                .baseUrl("http://localhost:" + 8080 + "/demo") // add context path to url in a servlet container.
                .build();
    }

    @AfterEach
    public void teardown() {
    }

    @Test
    public void getAllPostsWillBeOk() throws Exception {
        this.client
                .get().uri("/posts")
                .exchange()
                .expectStatus().isOk();
    }

}
