package com.example.demo;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.couchbase.CouchbaseServiceConnection;
import org.springframework.boot.test.autoconfigure.data.couchbase.DataCouchbaseTest;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.couchbase.BucketDefinition;
import org.testcontainers.couchbase.CouchbaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@DataCouchbaseTest
@Testcontainers
@Slf4j
public class CouchbaseIntegrationTests {

    private static final String COUCHBASE_IMAGE_NAME = "couchbase";
    private static final String DEFAULT_IMAGE_NAME = "couchbase/server";
    private static final DockerImageName DEFAULT_IMAGE = DockerImageName.parse(COUCHBASE_IMAGE_NAME)
        .asCompatibleSubstituteFor(DEFAULT_IMAGE_NAME);

    @Container
    @CouchbaseServiceConnection
    public static CouchbaseContainer COUCHBASE_CONTAINER = new CouchbaseContainer(DEFAULT_IMAGE)
        .withCredentials("Administrator", "password")
        .withBucket(new BucketDefinition("demo").withPrimaryIndex(true))
        .withStartupAttempts(5)
        .withStartupTimeout(Duration.ofSeconds(120));


//    @DynamicPropertySource
//    public static void registerCouchbaseProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.couchbase.connection-string", COUCHBASE_CONTAINER::getConnectionString);
//        registry.add("spring.couchbase.username", COUCHBASE_CONTAINER::getUsername);
//        registry.add("spring.couchbase.password", COUCHBASE_CONTAINER::getPassword);
//    }

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testDatabaseIsRunning() {
        assertThat(COUCHBASE_CONTAINER.isRunning()).isTrue();
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
