package com.example.demo.domain;

import com.example.demo.domain.model.Post;
import com.example.demo.domain.model.Status;
import com.example.demo.domain.repository.PostRepository;
import com.example.demo.domain.repository.Specifications;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDataSourceConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author hantsy
 */
@Slf4j
@DataJpaTest(excludeAutoConfiguration = EmbeddedDataSourceConfiguration.class)
@Testcontainers
public class PostRepositoryTestWithTestcontainers {

    @Container
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer<>("postgres:12")
        .withCopyFileToContainer(MountableFile.forClasspathResource("init.sql"), "/docker-entrypoint-initdb.d/init.sql");

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> "r2dbc:postgresql://"
            + postgreSQLContainer.getHost() + ":" + postgreSQLContainer.getFirstMappedPort()
            + "/" + postgreSQLContainer.getDatabaseName());
        registry.add("spring.r2dbc.username", () -> postgreSQLContainer.getUsername());
        registry.add("spring.r2dbc.password", () -> postgreSQLContainer.getPassword());
    }

    //@Inject
    @Autowired
    PostRepository posts;

    @SneakyThrows
    @BeforeEach
    public void setup() {
        log.debug("setup tests, clear data ...");
        this.posts.customDeleteAll();
    }

    @Test
    public void testSaveAll() {
        var data = List.of(
            Post.builder().title("test").content("content").status(Status.PENDING_MODERATION).build(),
            Post.builder().title("test1").content("content1").build());
        this.posts.saveAllAndFlush(data);

        var results = posts.findAll();
        assertThat(results.size()).isEqualTo(2);

        var resultsByKeyword = posts.findAll(Specifications.findByKeyword("", Status.PENDING_MODERATION), PageRequest.of(0, 10));
        assertThat(resultsByKeyword.getTotalElements()).isEqualTo(1);
    }

    @Test
    public void testInsertAndQuery() {
        var data = Post.builder().title("test").content("test content").status(Status.DRAFT).build();
        var saved = this.posts.save(data);
        this.posts.findById(saved.getId()).ifPresent(
            p -> assertThat(p.getStatus()).isEqualTo(Status.DRAFT)
        );

    }

    @Test
    public void testUpdateStatus() {
        var data = Post.builder().title("test").content("test content").status(Status.DRAFT).build();
        var saved = this.posts.save(data);
        log.debug("saved post : {}", saved);

        var updated = this.posts.updateStatus(saved.getId(), Status.PUBLISHED);
        log.debug("updated posts count: {}", updated);

        this.posts.findById(saved.getId()).ifPresent(
            p -> {
                log.debug("post after updated: {}", p);
                assertThat(p.getStatus()).isEqualTo(Status.PUBLISHED);
            }
        );
    }

}
