package com.example.demo;

import com.example.demo.client.PostClientService;
import io.rsocket.Closeable;
import io.rsocket.core.RSocketServer;
import io.rsocket.transport.netty.server.TcpServerTransport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import reactor.test.StepVerifier;


@SpringJUnitConfig(classes = Application.class)
public class IntegrationTests {

    @Value("${rsocket.port:7000}")
    int port;

    @Autowired
    PostClientService client;

    @Autowired
    RSocketServer rSocketServer;

    private Closeable disposableServer;

    @BeforeEach
    public void setup() {
        this.disposableServer = this.rSocketServer
                .bindNow(TcpServerTransport.create("localhost", port));
    }

    @AfterEach
    public void teardown() {
        this.disposableServer.dispose();
    }

    @Test
    public void testGetAllPosts() throws Exception {
        this.client.all()
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }

}
