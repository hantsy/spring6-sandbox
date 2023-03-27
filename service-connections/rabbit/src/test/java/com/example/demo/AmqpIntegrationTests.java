package com.example.demo;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.amqp.RabbitServiceConnection;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest()
@Testcontainers
@Slf4j
public class AmqpIntegrationTests {

    @Container
    @RabbitServiceConnection
    public static RabbitMQContainer RABBITMQ_CONTAINER = new RabbitMQContainer(DockerImageName.parse("rabbitmq:3.11-management"))
        .withAdminPassword("admin@123");

//    @DynamicPropertySource
//    private static void registerRedisProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.rabbitmq.host", RABBITMQ_CONTAINER::getHost);
//        registry.add("spring.rabbitmq.port", RABBITMQ_CONTAINER::getFirstMappedPort);
//        registry.add("spring.rabbitmq.username", RABBITMQ_CONTAINER::getAdminUsername);
//        registry.add("spring.rabbitmq.password", RABBITMQ_CONTAINER::getAdminPassword);
//    }

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Test
    void testServiceIsRunning() {
        assertThat(RABBITMQ_CONTAINER.isRunning()).isTrue();
    }

    @Test
    public void testSendMessage() {
        amqpTemplate.convertAndSend(
            DemoApplication.EXCHANGE_HELLO,
            DemoApplication.ROUTING_HELLO,
            new Greeting("Hello Rabbit")
        );
    }
}
