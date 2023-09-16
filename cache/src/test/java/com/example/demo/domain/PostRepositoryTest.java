package com.example.demo.domain;

import com.example.demo.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.MountableFile;

import java.util.Map;
import java.util.stream.IntStream;

@Slf4j
@SpringJUnitConfig(classes = {PostRepositoryTest.TestConfig.class})
@ContextConfiguration(initializers = {PostRepositoryTest.TestContainerInitializer.class})
public class PostRepositoryTest {

    @Autowired
    PostRepository posts;

    @Autowired
    TransactionTemplate tx;

    @SneakyThrows
    @BeforeEach
    public void setup() {
        this.posts.deleteAll();
    }

    @Test
    public void testSaveAll() {
        var data = Post.of("test", "content");
        var saved = tx.execute(__ -> this.posts.save(data));

        log.debug("saved post: {}", saved);

        IntStream.range(1, 11)
                .forEach(i -> {
                    log.debug(" find by id #{} ", i);
                    this.posts.findById(saved.id());
                });

        var updatedPost = new Post(saved.id(), "updated title", saved.content());
        var updated = tx.execute(__ -> this.posts.save(updatedPost));

        log.debug("updated: {}", updated);

        IntStream.range(11, 21)
                .forEach(i -> {
                    log.debug(" find by id #{} ", i);
                    this.posts.findById(updated.id());
                });
    }

    //see: https://github.com/testcontainers/testcontainers-java/discussions/4841
    static class TestContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        final PostgreSQLContainer container = new PostgreSQLContainer<>("postgres:12")
                .withCopyFileToContainer(
                        MountableFile.forClasspathResource("init.sql"),
                        "/docker-entrypoint-initdb.d/init.sql"
                );

        @Override
        public void initialize(ConfigurableApplicationContext context) {
            container.start();

            log.info(" container.getFirstMappedPort():: {}", container.getFirstMappedPort());
            context.addApplicationListener(event -> {
                if (event instanceof ContextClosedEvent) {
                    container.stop();
                }
            });

            context.getEnvironment().getPropertySources()
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

    @Configuration
    @Import(value = {
            JdbcConfig.class,
            DataSourceConfig.class,
            DataJdbcConfig.class,
            CacheConfig.class
    })
    static class TestConfig {
    }
}
