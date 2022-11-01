package com.example.demo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.JdkClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.multipart.FilePartEvent;
import org.springframework.http.codec.multipart.FormPartEvent;
import org.springframework.http.codec.multipart.PartEvent;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import reactor.test.StepVerifier;

import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;

/**
 * @author hantsy
 */
@SpringJUnitConfig(classes = Application.class)
public class IntegrationTests {

    @Value("${server.port:8080}")
    int port;

    WebClient client;

    @Autowired
    HttpServer httpServer;

    private DisposableServer disposableServer;

    @BeforeEach
    public void setup() {
        var clientConnector = new ReactorClientHttpConnector();

        this.disposableServer = this.httpServer.bindNow();
        this.client = WebClient.builder()
                .baseUrl("http://localhost:" + this.port)
                .defaultHeaders(headers -> headers.set("X-APIKEY", "test-key"))
                .clientConnector(clientConnector)
                .codecs(clientCodecConfigurer -> clientCodecConfigurer.registerDefaults(true))
                .build();
    }

    @AfterEach
    public void teardown() {
        this.disposableServer.dispose();
    }

    @Test
    public void testPartEvents() throws Exception {
        this.client
                .post().uri("/partevents")
                .contentType(MULTIPART_FORM_DATA)
                .body(
                        Flux.concat(
                                FormPartEvent.create("name", "test"),
                                FilePartEvent.create("file", new ClassPathResource("spring.png"))
                        ),
                        PartEvent.class
                )
                .exchangeToFlux(clientResponse -> {
                            assertThat(clientResponse.statusCode()).isEqualTo(HttpStatus.OK);
                            return clientResponse.bodyToFlux(String.class);
                        }
                )
                .as(StepVerifier::create)
//                .consumeNextWith(it-> assertThat(it).isEqualTo("test"))
//                .consumeNextWith(it ->assertThat(it).isEqualTo("spring.png"))
                .expectNextCount(2)
                .verifyComplete();
    }

}
