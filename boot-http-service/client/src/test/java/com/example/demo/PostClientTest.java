package com.example.demo;


import com.example.demo.client.ClientApplication;
import com.example.demo.shared.PostApi;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.test.StepVerifier;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(classes = ClientApplication.class)
@WireMockTest(httpPort = 8080)
public class PostClientTest {

    @Autowired
    PostApi postClient;

    @BeforeEach
    public void setup() {
    }

    @Test
    public void testGetPostById() {
        var id = UUID.randomUUID();
        stubFor(get("/posts/" + id)
                .willReturn(
                        aResponse()
                                .withStatus(404)
                                .withStatusMessage("Not Found")
                )
        );

        postClient.getById(id)
                .as(StepVerifier::create)
                .expectError(WebClientResponseException.class)
                .verify();

        verify(getRequestedFor(urlEqualTo("/posts/" + id))
                .withHeader("Accept", equalTo("application/json"))
        );
    }

}
