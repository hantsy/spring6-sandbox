package com.example.demo;


import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest()
@Testcontainers
@Slf4j
public class KafkaIntegrationTests {

    @Container
    @ServiceConnection
    public static KafkaContainer KAFKA_CONTAINER = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"))
        .withEmbeddedZookeeper();

//    @DynamicPropertySource
//    private static void registerKafkaProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.kafka.bootstrap-servers", () -> KAFKA_CONTAINER.getHost() + ":" + KAFKA_CONTAINER.getFirstMappedPort());
//    }

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private GreetingListener listener;

    @Test
    void testServiceIsRunning() {
        assertThat(KAFKA_CONTAINER.isRunning()).isTrue();
    }

    @Test
    public void testSendMessage() {
        kafkaTemplate.send(DemoApplication.TOPIC_HELLO, new Greeting("Hello Kafka"));
        Awaitility.waitAtMost(Duration.ofSeconds(30))
            .untilAsserted(() -> assertThat(this.listener.messages).containsExactly("Hello Kafka"));
    }
}
