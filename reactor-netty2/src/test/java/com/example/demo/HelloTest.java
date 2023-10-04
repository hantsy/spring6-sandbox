package com.example.demo;


import com.example.demo.client.HelloClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import reactor.netty5.DisposableServer;
import reactor.netty5.http.server.HttpServer;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = Application.class)
public class HelloTest {

    @Value("${server.port:8080}")
    int port;


    @Autowired
    HttpServer httpServer;

    private DisposableServer disposableServer;

    @Autowired
    HelloClient helloClient;


    @BeforeEach
    public void setup() {
        this.disposableServer = this.httpServer.bindNow();
    }

    @AfterEach
    public void teardown() {
        if (this.disposableServer != null) {
            this.disposableServer.dispose();
        }
    }

    @Test
    public void testGetAllPosts() throws Exception {
        helloClient.greet("Hantsy")
                .as(StepVerifier::create)
                .consumeNextWith(result -> assertThat(result).isEqualTo("Hello, Hantsy"))
                .verifyComplete();
    }

}
