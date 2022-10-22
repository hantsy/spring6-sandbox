package com.example.demo.it;

import com.example.demo.event.transactional.PostCreated;
import com.example.demo.web.CreatePostCommand;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class SseIntegrationTests {

    WebClient client;

    @BeforeEach
    public void setup() {
        this.client = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector())
                .codecs(ClientCodecConfigurer::defaultCodecs)
                .exchangeStrategies(ExchangeStrategies.withDefaults())
                .baseUrl("http://localhost:" + 8080 + "/demo")
                .build();
    }

    @AfterEach
    public void teardown() {
    }

    @SneakyThrows
    @Test
    public void testSseEndpoints() {
        var verifier = this.client.get().uri("/events")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchangeToFlux(clientResponse -> clientResponse.bodyToFlux(PostCreated.class))
                .log()
                .as(StepVerifier::create)
                .consumeNextWith(it -> assertThat(it.title()).isEqualTo("test1"))
                .consumeNextWith(it -> assertThat(it.title()).isEqualTo("test2"))
                .thenCancel()
                .verifyLater();

        // create posts.
        this.client.post().uri("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CreatePostCommand("test1", "content of test"))
                .exchange().then().block();
        this.client.post().uri("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CreatePostCommand("test2", "content of test2"))
                .exchange().then().block();

        verifier.verify(Duration.ofMillis(5_000L));
    }

}
