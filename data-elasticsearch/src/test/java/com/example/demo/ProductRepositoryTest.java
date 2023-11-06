package com.example.demo;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.env.MapPropertySource;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = ProductRepositoryTest.TestConfig.class)
@ActiveProfiles("test")
@ContextConfiguration(initializers = ProductRepositoryTest.TestContainerInitializer.class)
@Slf4j
public class ProductRepositoryTest {
    static class TestContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            final ElasticsearchContainer container = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:7.17.9");

            container.start();
            log.info(" container.getFirstMappedPort():: {}", container.getFirstMappedPort());
            configurableApplicationContext
                    .addApplicationListener((ApplicationListener<ContextClosedEvent>) event -> container.stop());
            configurableApplicationContext.getEnvironment().
                    getPropertySources()
                    .addFirst(
                            new MapPropertySource("testproperties",
                                    Map.of("elasticsearch.uris", container.getHttpHostAddress())
                            )
                    );

        }
    }

    @Configuration
    @ComponentScan(basePackageClasses = Product.class)
    @Import(ElasticsearchConfig.class)
    static class TestConfig {

    }

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

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

        assertThat(elasticsearchTemplate.get(product.id(), Product.class).name()).isEqualTo("test");
    }
}
