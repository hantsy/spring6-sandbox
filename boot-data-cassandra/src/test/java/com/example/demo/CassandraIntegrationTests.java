package com.example.demo;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.cassandra.DataCassandraTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataCassandraTest
@Testcontainers
@Slf4j
public class CassandraIntegrationTests {

    @Container
    @ServiceConnection
    public static CassandraContainer<?> CASSANDRA_CONTAINER = new CassandraContainer<>(DockerImageName.parse("cassandra"))
        .withInitScript("init.cql")
        .withStartupTimeout(Duration.ofMinutes(5));

//    @DynamicPropertySource
//    static void bindCassandraProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.cassandra.keyspace-name", () -> "demo");
//        registry.add("spring.cassandra.contact-points", () -> "localhost:" + CASSANDRA_CONTAINER.getMappedPort(9042));
//        registry.add("spring.cassandra.local-datacenter", () -> "datacenter1");
//        registry.add("spring.cassandra.schema-action", () -> "RECREATE");
//    }

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testDatabaseIsRunning() {
        assertThat(CASSANDRA_CONTAINER.isRunning()).isTrue();
    }

    @Test
    public void testProductRepository() {
        var product = productRepository.save(new Product(UUID.randomUUID().toString(), "test", BigDecimal.ONE));
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
