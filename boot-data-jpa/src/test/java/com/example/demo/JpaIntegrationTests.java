package com.example.demo;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Window;
import org.springframework.data.support.WindowIterator;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest()
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Slf4j
public class JpaIntegrationTests {

    @Container
    @ServiceConnection
    public static PostgreSQLContainer<?> PG_CONTAINER = new PostgreSQLContainer<>(DockerImageName.parse("postgres:14"));

//    @DynamicPropertySource
//    private static void registerJdbcProperties(DynamicPropertyRegistry registry) {
//        log.debug("url: {}", PG_CONTAINER.getJdbcUrl());
//        registry.add("spring.datasource.url", PG_CONTAINER::getJdbcUrl);
//        registry.add("spring.datasource.username", PG_CONTAINER::getUsername);
//        registry.add("spring.datasource.password", PG_CONTAINER::getPassword);
//    }

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setup() {
        productRepository.deleteAllInBatch();
    }

    @Test
    void testDatabaseIsRunning() {
        assertThat(PG_CONTAINER.isRunning()).isTrue();
    }

    @Test
    public void testProductRepository() {
        var product = productRepository.save(Product.of(null, "test", BigDecimal.ONE));
        assertThat(product).isNotNull();
        assertThat(product.getId()).isNotNull();

        productRepository.findById(product.getId()).ifPresent(
                p -> {
                    log.debug("found product by id: {}", p);
                    assertThat(p.getName()).isEqualTo("test");
                }
        );
    }

    @Test
    public void testScrollAPI() {
        productRepository.saveAllAndFlush(
                List.of(
                        Product.of(null, "Apple", BigDecimal.ONE),
                        Product.of(null, "Orange", BigDecimal.TEN),
                        Product.of(null, "WaterMelon", BigDecimal.TEN),
                        Product.of(null, "Melon", BigDecimal.TEN),
                        Product.of(null, "Tomato", new BigDecimal("4.5"))
                )
        );

        Window<Product> products = productRepository.findFirst10ByNameContains("Melon", ScrollPosition.offset(0));

        do {
            for (Product product : products) {
                log.debug("found product: {}", product);
            }

            products = productRepository.findFirst10ByNameContains("Apple", products.positionAt(products.size() - 1));

        } while (!products.isEmpty() && products.hasNext());

        WindowIterator<Product> productWindowIterator = WindowIterator.of(position -> productRepository.findFirst10ByNameContains("Apple", position))
                .startingAt(ScrollPosition.offset());

        while (productWindowIterator.hasNext()) {
            var product = productWindowIterator.next();
            log.debug("product windows iterator: {}", product);
        }

        var byExamples = productRepository.findBy(
                Example.of(
                        Product.of(null, "Melon", null),
                        ExampleMatcher
                                .matching()
                                .withIgnoreNullValues()
                                .withMatcher("name", ExampleMatcher.GenericPropertyMatcher::contains)
                ),
                q -> q.limit(10).scroll(ScrollPosition.offset())
        );
        do {
            for (Product product : byExamples) {
                log.debug("productByExample windows iterator: {}", product);
            }

        } while (!byExamples.isEmpty() && byExamples.hasNext());

    }


}
