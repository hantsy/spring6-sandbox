package com.example.demo.it;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;

@Slf4j
public class IntegrationTests {

    WebTestClient client;

    @BeforeEach
    public void setup() {
        var baseUrl = "http://localhost:8080";
        if (System.getenv().containsKey("BASE_API_URL")) {
            baseUrl = System.getenv("BASE_API_URL");
        }
        log.debug("baseUrl is: {}", baseUrl);
        this.client = WebTestClient
                .bindToServer()
                .responseTimeout(Duration.ofMillis(5000))
                .baseUrl(baseUrl)
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
