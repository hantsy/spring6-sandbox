package com.example.demo;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.autoconfigure.data.redis.RedisServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataRedisTest
@Testcontainers
@Slf4j
public class RedisIntegrationTests {

    @Container
    @RedisServiceConnection
    public static GenericContainer REDIS_CONTAINER = new GenericContainer(DockerImageName.parse("redis:5.0.3-alpine"))
        .withExposedPorts(6379);

//    @DynamicPropertySource
//    private static void registerRedisProperties(DynamicPropertyRegistry registry) {
//        log.debug("host: {}", REDIS_CONTAINER.getHost());
//        log.debug("port: {}", REDIS_CONTAINER.getFirstMappedPort());
//        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
//        registry.add("spring.data.redis.port", REDIS_CONTAINER::getFirstMappedPort);
//    }

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testRedisIsRunning() {
        assertThat(REDIS_CONTAINER.isRunning()).isTrue();
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
