package com.example.demo;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.autoconfigure.data.redis.RedisServiceConnection;
import org.springframework.boot.test.autoconfigure.neo4j.Neo4jServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataNeo4jTest
@Testcontainers
@Slf4j
public class Neo4jIntegrationTests {

    @Container
    //@Neo4jServiceConnection
    public static Neo4jContainer<?> NEO4J_CONTAINER = new Neo4jContainer<>(DockerImageName.parse("neo4j:4.4"))
        .withoutAuthentication();

    @DynamicPropertySource
    private static void registerRedisProperties(DynamicPropertyRegistry registry) {
        log.debug("url: {}", NEO4J_CONTAINER.getBoltUrl());
        registry.add("spring.neo4j.uri", NEO4J_CONTAINER::getBoltUrl);
    }

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testNeo4jIsRunning() {
        assertThat(NEO4J_CONTAINER.isRunning()).isTrue();
    }

    @Test
    public void testProductRepository() {
        var product = productRepository.save(new Product(null, "test", BigDecimal.ONE));
        assertThat(product).isNotNull();
        assertThat(product.id()).isNotNull();

        productRepository.findById(product.id()).ifPresent(
            p -> {
                log.debug("found product by id: {}", p);
                assertThat(p.name()).isEqualTo("test");
            }
        );
    }


}
