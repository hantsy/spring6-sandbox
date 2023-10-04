package com.example.demo;


import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClientBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.elasticsearch.RestClientBuilderCustomizer;
import org.springframework.boot.test.autoconfigure.data.elasticsearch.DataElasticsearchTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataElasticsearchTest(
        properties = { "spring.elasticsearch.username=elastic", "spring.elasticsearch.password=changeme" }
)
@Testcontainers
@Slf4j
public class Elasticsearch8IntegrationTests {
    private final static String IMAGE_NAME = "docker.elastic.co/elasticsearch/elasticsearch:8.10.2";

    @Container
    public static ElasticsearchContainer ES_CONTAINER = new ElasticsearchContainer(IMAGE_NAME);

    @DynamicPropertySource
    static void registerElasticsearchProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.elasticsearch.uris", () -> "https://" + ES_CONTAINER.getHttpHostAddress());
    }

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

    @TestConfiguration
    static class SSL {

        @Bean
        public RestClientBuilderCustomizer customizer() {
            return new RestClientBuilderCustomizer() {
                @Override
                public void customize(RestClientBuilder builder) {

                }

                @Override
                public void customize(HttpAsyncClientBuilder builder) {
                    builder.setSSLContext(ES_CONTAINER.createSslContextFromCa());
                }
            };
        }

    }
}
