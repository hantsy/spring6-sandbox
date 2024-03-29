package com.example.demo;


import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringJUnitConfig(classes = Application.class)
public class ApplicationTests {

    @Autowired
    ApplicationContext context;

    WebTestClient rest;

    @BeforeEach
    public void setup() {
        this.rest = WebTestClient
                .bindToApplicationContext(this.context)
                .configureClient()
                .build();
    }

    @Test
    public void testGreeting() throws Exception {
        this.rest
                .get()
                .uri("/greeting")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).value(CoreMatchers.equalTo("Hello, World"));
    }

}
