package com.example.demo;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest()
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Slf4j
public class JdbcIntegrationTests {

    @Container
    @JdbcServiceConnection
    public static PostgreSQLContainer<?> PG_CONTAINER = new PostgreSQLContainer<>(DockerImageName.parse("postgres:14"));

//    @DynamicPropertySource
//    private static void registerRedisProperties(DynamicPropertyRegistry registry) {
//        log.debug("url: {}", PG_CONTAINER.getJdbcUrl());
//        registry.add("spring.datasource.url", PG_CONTAINER::getJdbcUrl);
//        registry.add("spring.datasource.username", PG_CONTAINER::getUsername);
//        registry.add("spring.datasource.password", PG_CONTAINER::getPassword);
//    }

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testDatabaseIsRunning() {
        assertThat(PG_CONTAINER.isRunning()).isTrue();
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
