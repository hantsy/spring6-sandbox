package com.example.demo.it;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;

public class IntegrationTests {

    WebTestClient client;

    @BeforeEach
    public void setup() {
        this.client = WebTestClient
                .bindToServer()
                .responseTimeout(Duration.ofSeconds(5))
                .baseUrl("http://localhost:" + 8080 + "/demo")
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
                .expectStatus()
                .is3xxRedirection()
                .expectHeader().location("http://localhost:" + 8080 + "/demo/login");
    }

}
