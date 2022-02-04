package com.example.demo.testcontainers;

import com.example.demo.DataSourceConfig;
import com.example.demo.domain.JdbcConfig;
import com.example.demo.domain.model.PopularPost;
import com.example.demo.domain.repository.PopularPostRepository;
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

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author hantsy
 */
@Slf4j
@SpringJUnitConfig(classes = {PopularPostRepositoryTestWithTestcontainers.TestConfig.class})
@ContextConfiguration(initializers = PopularPostRepositoryTestWithTestcontainers.TestContainerInitializer.class)
public class PopularPostRepositoryTestWithTestcontainers {

    //see: https://github.com/testcontainers/testcontainers-java/discussions/4841
    static class TestContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            var container = new PostgreSQLContainer<>("postgres:12")
                    .withCopyFileToContainer(MountableFile.forClasspathResource("init.sql"), "/docker-entrypoint-initdb.d/init.sql");
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
    @Import({DataSourceConfig.class})
    @ComponentScan(basePackageClasses = {JdbcConfig.class})
    static class TestConfig {
    }

    @Autowired
    PopularPostRepository posts;

    @SneakyThrows
    @BeforeEach
    public void setup() {
        this.posts.deleteAll();
    }

    @Test
    public void testInsertAndQuery() {
        var data = new PopularPost(null, "test", "test content", null, null, null);
        var inserted = this.posts.save(data);
        assertThat(inserted.id()).isNotNull();
        assertThat(inserted.title()).isEqualTo("test");
        assertThat(inserted.content()).isEqualTo("test content");
        assertThat(inserted.createdAt()).isNotNull();
        assertThat(inserted.version()).isGreaterThanOrEqualTo(0L);

        var existed = this.posts.findById(inserted.id()).get();
        assertThat(existed.title()).isEqualTo("test");
        assertThat(existed.content()).isEqualTo("test content");

        var updated = this.posts.save(new PopularPost(existed.id(), "updated test", "updated content", existed.createdAt(), existed.createdBy(), existed.version()));
        assertThat(updated.title()).isEqualTo("updated test");
        assertThat(updated.content()).isEqualTo("updated content");
        assertThat(updated.version()).isGreaterThanOrEqualTo(1L);
    }

}
