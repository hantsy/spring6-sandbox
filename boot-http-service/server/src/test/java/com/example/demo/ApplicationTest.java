package com.example.demo;


import com.example.demo.server.ServerApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

@SpringBootTest(classes = ServerApplication.class)
@AutoConfigureWebTestClient
public class ApplicationTest {

    @Autowired
    WebTestClient client;

    @BeforeEach
    public void setup() {
    }

    @Test
    public void testGetAllPosts() {
        client.get().uri("/posts")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void testGetPost_NotFound() {
        var id = UUID.randomUUID();
        client.get().uri("/posts/{id}", id)
                .exchange()
                .expectStatus().isNotFound();
    }

}
