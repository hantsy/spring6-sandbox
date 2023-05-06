package com.example.demo;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataR2dbcTest
//@Testcontainers
@Slf4j
public class ImportTestcontainersExampleTests {

    @TestConfiguration(proxyBeanMethods = false)
    @ImportTestcontainers(MyContainers.class)
    static class MyTestConfig {
    }

    interface MyContainers {
        @Container
        PostgreSQLContainer<?> PG_CONTAINER = new PostgreSQLContainer<>(DockerImageName.parse("postgres:14"));
    }

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testProductRepository() {
        var product = productRepository.save(new Product(null, "test", BigDecimal.ONE));

        product.as(StepVerifier::create)
                .consumeNextWith(p -> {
                    assertThat(p).isNotNull();
                    assertThat(p.id()).isNotNull();
                })
                .verifyComplete();
    }


}
