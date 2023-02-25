package com.example.demo.testcontainers;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.env.MapPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Map;

class TestContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private final Logger log = LoggerFactory.getLogger(TestContainerInitializer.class);

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        final PostgreSQLContainer container = new PostgreSQLContainer("postgres:12");

        container.start();
        log.info(" container.getFirstMappedPort():: {}", container.getFirstMappedPort());
        configurableApplicationContext
            .addApplicationListener((ApplicationListener<ContextClosedEvent>) event -> container.stop());
        configurableApplicationContext
            .getEnvironment()
            .getPropertySources()
            .addFirst(
                new MapPropertySource("testdatasource",
                    Map.of("datasource.url", container.getJdbcUrl(),
                        "datasource.username", container.getUsername(),
                        "datasource.password", container.getPassword()
                    )
                )
            );

    }
}
