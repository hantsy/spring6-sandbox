package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("it")
class IntegrationTests {

    private WebClient webClient;

    @LocalServerPort
    private int port = 8080;

    @BeforeEach
    public void setup() {
        webClient = WebClient.builder()
            .baseUrl("http://localhost:" + this.port)
            .build();
    }

    @Test
    void testGetAllEndpoints() {
        this.webClient.get().uri("/products")
            .exchangeToFlux(clientResponse -> {
                assertThat(clientResponse.statusCode()).isEqualTo(HttpStatus.OK);
                return clientResponse.bodyToFlux(Product.class);
            })
            .as(StepVerifier::create)
            .expectNextCount(2)
            .expectComplete();
    }

}
