package com.example.demo.domain;

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
import org.testcontainers.utility.MountableFile;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author hantsy
 */
@Slf4j
@SpringJUnitConfig(classes = {R2dbcConfig.class, PostRepositoryTest.TestConfig.class})
@ContextConfiguration(initializers = PostRepositoryTest.TestContainerInitializer.class)
public class PostRepositoryTest {

    static class TestContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            var container = new PostgreSQLContainer<>("postgres:12")
                    .withCopyFileToContainer(
                            MountableFile.forClasspathResource("init.sql"),
                            "/docker-entrypoint-initdb.d/init.sql"
                    );
            container.start();
            configurableApplicationContext.addApplicationListener((ApplicationListener<ContextClosedEvent>) event ->
                    container.stop()
            );
            configurableApplicationContext.getEnvironment()
                    .getPropertySources()
                    .addFirst(
                            new MapPropertySource("testdatasource",
                                    Map.of("r2dbc.host", container.getHost(),
                                            "r2dbc.port", container.getFirstMappedPort(),
                                            "r2dbc.username", container.getUsername(),
                                            "r2dbc.password", container.getPassword(),
                                            "r2dbc.database", container.getDatabaseName()
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
        var latch = new CountDownLatch(1);
        this.posts.deleteAll().doOnTerminate(latch::countDown)
                .subscribe(del -> log.debug("deleted posts: {}", del));
        latch.await(5000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testSaveAll() {

        var data = Post.builder().title("test").content("content")
                .status(Status.PENDING_MODERATION)
                .build();
        var data1 = Post.builder().title("test1").content("content1").build();

        var result = posts.saveAll(List.of(data, data1)).log("[Generated result]")
                .doOnNext(id -> log.debug("generated id: {}", id));

        assertThat(result).isNotNull();
        result.as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();

        StepVerifier.create(posts.countByStatus())
                .consumeNextWith(r -> {
                    log.debug("data: {}", r);
                    assertThat(r.get("status")).isEqualTo(Status.DRAFT);
                })
                .consumeNextWith(r -> {
                    log.debug("data: {}", r);
                    assertThat(r.get("cnt")).isEqualTo(1L);
                    assertThat(r.get("status")).isEqualTo(Status.PENDING_MODERATION);
                })
                .verifyComplete();
    }

    @Test
    public void testInsertAndQuery() {
        var data = Post.builder().title("test").content("content")
                .status(Status.PENDING_MODERATION)
                .build();
        this.posts.save(data)
                .flatMap(id -> this.posts.findById(id))
                .as(StepVerifier::create)
                .consumeNextWith(r -> {
                    log.debug("result data: {}", r);
                    assertThat(r.getStatus()).isEqualTo(Status.PENDING_MODERATION);
                })
                .verifyComplete();
    }

    @Configuration
    @ComponentScan
    static class TestConfig {
    }

}
