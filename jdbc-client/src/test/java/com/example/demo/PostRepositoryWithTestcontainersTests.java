package com.example.demo;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.MountableFile;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author hantsy
 */
@Slf4j
@SpringJUnitConfig(classes = {PostRepositoryWithTestcontainersTests.TestConfig.class})
@ContextConfiguration(initializers = PostRepositoryWithTestcontainersTests.TestContainerInitializer.class)
public class PostRepositoryWithTestcontainersTests {

    //see: https://github.com/testcontainers/testcontainers-java/discussions/4841
    static class TestContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            var container = new PostgreSQLContainer<>("postgres:12")
                    .withCopyFileToContainer(
                            MountableFile.forClasspathResource("init.sql"),
                            "/docker-entrypoint-initdb.d/init.sql"
                    );
            //.withInitScript("init.sql");
            container.start();
            log.info(" container.getFirstMappedPort():: {}", container.getFirstMappedPort());
            configurableApplicationContext
                    .addApplicationListener((ApplicationListener<ContextClosedEvent>) event -> container.stop());
            var env = configurableApplicationContext.getEnvironment();
            var props = env.getPropertySources();
            props.addFirst(
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
    @Import(DataSourceConfig.class)
    @ComponentScan(basePackageClasses = {JdbcConfig.class})
    static class TestConfig {
    }


    @Autowired
    PostRepository posts;

    @SneakyThrows
    @BeforeEach
    public void setup() {
        var deleted = this.posts.deleteAll();
        log.debug("deleted posts: {}", deleted);
    }

    @Test
    public void testSaveAll() {
        var data = List.of(
                Post.of("test", "content", Status.PENDING_MODERATION),
                Post.of("test2", "content2"),
                Post.of("test3", "content3")
        );

        data.forEach(post -> {
            var saved = posts.save(post);
            log.debug("saved post: {}", saved);
        });

        Long count = posts.count();
        assertThat(count).isEqualTo(3L);

        var countByStatus = posts.countByStatus();
        log.debug("count by status: {}", countByStatus);
        assertThat(countByStatus.get(Status.DRAFT)).isEqualTo(2L);
    }

    @Test
    public void testInsertAndQuery() {
        var id = this.posts.save(Post.of("test title", "test content"));
        var saved = this.posts.findById(id);
        assertThat(saved.status()).isEqualTo(Status.DRAFT);

        var updatedCnt = this.posts.update(new Post(saved.id(), "updated test", "updated content", Status.PENDING_MODERATION, saved.createdAt()));
        assertThat(updatedCnt).isEqualTo(1);
        var updated = this.posts.findById(id);
        assertThat(updated.status()).isEqualTo(Status.PENDING_MODERATION);
    }

}
