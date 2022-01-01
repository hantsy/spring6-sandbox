package com.example.demo.domain;

import com.example.demo.DataSourceConfig;
import com.example.demo.domain.model.CreatePostCommand;
import com.example.demo.domain.model.Post;
import com.example.demo.domain.model.Status;
import com.example.demo.domain.repository.PostRepository;
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
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author hantsy
 */
@Slf4j
@SpringJUnitConfig(classes = {DataSourceConfig.class, PostRepositoryTest.TestConfig.class})
@ContextConfiguration(initializers = PostRepositoryTest.TestContainerInitializer.class)
public class PostRepositoryTest {

    static class TestContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            var container = new PostgreSQLContainer("postgres:12");
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

        var data = new Post("test", "content", Status.PENDING_MODERATION);
        var data1 = new Post("test1", "content1");

        var result = posts.saveAll(List.of(data, data1));

        assertThat(result).isNotNull();
        assertThat(result.length).isEqualTo(2);

        var countByStatus = posts.countByStatus();

        assertThat(countByStatus.get(0).get("status")).isEqualTo(Status.DRAFT);
        var item2 = countByStatus.get(1);
        assertThat(item2.get("cnt")).isEqualTo(1L);
        assertThat(item2.get("status")).isEqualTo(Status.PENDING_MODERATION);

    }

    @Test
    public void testInsertAndQuery() {
        var data = new CreatePostCommand("test", "content");
        var id = this.posts.save(data);
        var saved = this.posts.findById(id);
        assertThat(saved.status()).isEqualTo(Status.DRAFT);
    }

    @Configuration
    @ComponentScan
    static class TestConfig {
    }

}
