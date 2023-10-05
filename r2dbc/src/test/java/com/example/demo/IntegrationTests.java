package com.example.demo;

import com.example.demo.domain.model.Post;
import io.netty.channel.ChannelOption;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.DisposableServer;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.server.HttpServer;
import reactor.test.StepVerifier;

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
        var reactorHttpClient = HttpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5_000);
        var clientConnector = new ReactorClientHttpConnector(reactorHttpClient);

        this.disposableServer = this.httpServer.bindNow();
        this.client = WebClient.builder()
                .baseUrl("http://localhost:" + this.port)
                .defaultHeaders(headers -> headers.set("X-APIKEY", "test-key"))
                .clientConnector(clientConnector)
                .build();
    }

    @AfterEach
    public void teardown() {
        this.disposableServer.dispose();
    }

    @Test
    public void testGetAllPosts() throws Exception {
        this.client
                .get().uri("/posts")
                .retrieve()
                .bodyToFlux(Post.class)
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }

}
