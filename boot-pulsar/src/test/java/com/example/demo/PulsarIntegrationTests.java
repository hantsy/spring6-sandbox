package com.example.demo;


import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.PulsarClientException;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.pulsar.core.PulsarTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PulsarContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest()
@Testcontainers
@Slf4j
public class PulsarIntegrationTests {

    @Container
    @ServiceConnection
    static PulsarContainer PULSAR_CONTAINER = new PulsarContainer(DockerImageName.parse("apachepulsar/pulsar:3.1.0"));


//    @DynamicPropertySource
//    private static void registerKafkaProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.pulsar.client.service-url", () -> PULSAR_CONTAINER.getPulsarBrokerUrl());
//    }

    @Autowired
    private PulsarTemplate<Greeting> pulsarTemplate;

    @Autowired
    private GreetingListener listener;

    @Test
    void testServiceIsRunning() {
        assertThat(PULSAR_CONTAINER.isRunning()).isTrue();
    }

    @Test
    public void testSendMessage() throws PulsarClientException {
        pulsarTemplate.send(DemoApplication.TOPIC_HELLO, new Greeting("Hello Pulsar"));
        Awaitility.waitAtMost(Duration.ofSeconds(30))
                .untilAsserted(() -> assertThat(this.listener.messages).containsExactly("Hello Pulsar"));
    }
}
