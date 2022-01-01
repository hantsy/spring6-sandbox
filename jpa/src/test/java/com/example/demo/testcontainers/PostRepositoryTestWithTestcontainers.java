package com.example.demo.testcontainers;

import com.example.demo.DataSourceConfig;
import com.example.demo.domain.JpaConfig;
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
@SpringJUnitConfig(classes = {DataSourceConfig.class, JpaConfig.class, PostRepositoryTestWithTestcontainers.TestConfig.class})
@ContextConfiguration(initializers = PostRepositoryTestWithTestcontainers.TestContainerInitializer.class)
public class PostRepositoryTestWithTestcontainers {
    static class TestContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            final PostgreSQLContainer container = new PostgreSQLContainer("postgres:12");
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

    //@Inject
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
                Post.builder().title("test").content("content").status(Status.PENDING_MODERATION).build(),
                Post.builder().title("test1").content("content1").build());
        data.forEach(this.posts::save);

        var results = posts.findAll();
        assertThat(results.size()).isEqualTo(2);

        var resultsByKeyword = posts.findByKeyword("", Status.PENDING_MODERATION, 0, 10);
        assertThat(resultsByKeyword.size()).isEqualTo(1);
    }

    @Test
    public void testInsertAndQuery() {
        var data = Post.builder().title("test").content("test content").status(Status.DRAFT).build();
        var saved = this.posts.save(data);
        this.posts.findById(saved.getId()).ifPresent(
                p -> assertThat(p.getStatus()).isEqualTo(Status.DRAFT)
        );

    }

    @Configuration
    @ComponentScan(basePackageClasses = JpaConfig.class)
    static class TestConfig {
    }

}
