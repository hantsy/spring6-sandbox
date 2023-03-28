package com.example.demo;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.elasticsearch.DataElasticsearchTest;
import org.springframework.boot.test.autoconfigure.elasticsearch.ElasticsearchServiceConnection;
import org.springframework.boot.test.autoconfigure.mongo.MongoServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataElasticsearchTest
@Testcontainers
@Slf4j
public class ElasticsearchIntegrationTests {

    @Container
    @ElasticsearchServiceConnection
    public static ElasticsearchContainer ES_CONTAINER = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:7.17.9")
        .withEnv("discovery.type", "single-node");

//    @DynamicPropertySource
//    static void registerElasticsearchProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.elasticsearch.uris", ES_CONTAINER::getHttpHostAddress);
//    }

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testDatabaseIsRunning() {
        assertThat(ES_CONTAINER.isRunning()).isTrue();
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
