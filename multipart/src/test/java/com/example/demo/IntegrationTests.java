package com.example.demo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.JdkClientHttpConnector;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty5.DisposableServer;
import reactor.netty5.http.server.HttpServer;

import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.util.concurrent.Executors;

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
        var jvmHttpClient = HttpClient.newBuilder()
                .executor(Executors.newCachedThreadPool())
                .version(Version.HTTP_2)
                .build();
        var clientConnector = new JdkClientHttpConnector(jvmHttpClient);

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
//        this.client
//                .get().uri("/posts")
//                .retrieve()
//                .bodyToFlux(Post.class)
//                .as(StepVerifier::create)
//                .expectNextCount(2)
//                .verifyComplete();
    }

}
