package com.example.demo.testcontainers;

import com.example.demo.DataSourceConfig;
import com.example.demo.domain.DataJpaConfig;
import com.example.demo.domain.JpaConfig;
import com.example.demo.domain.model.Post;
import com.example.demo.domain.model.Status;
import com.example.demo.domain.repository.PostRepository;
import com.example.demo.domain.repository.Specifications;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author hantsy
 */
@Slf4j
@SpringJUnitConfig(classes = {DataSourceConfig.class, JpaConfig.class, DataJpaConfig.class, PostRepositoryTestWithTestcontainers.TestConfig.class})
@ContextConfiguration(initializers = TestContainerInitializer.class)
public class PostRepositoryTestWithTestcontainers {

    //@Inject
    @Autowired
    PostRepository posts;

    @SneakyThrows
    @BeforeEach
    public void setup() {
        log.debug("setup tests, clear data ...");
        this.posts.deleteAll();
    }

    @Test
    public void testSaveAll() {

        var data = List.of(
            Post.builder().title("test").content("content").status(Status.PENDING_MODERATION).build(),
            Post.builder().title("test1").content("content1").build()
        );
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

        // update the post
        var updateCount = this.posts.updateStatus(saved.getId(), Status.PUBLISHED);
        log.debug("updated posts count: {}", updateCount);

        this.posts.findById(saved.getId()).ifPresent(
            p -> {
                log.debug("after updated: {}", p);
                assertThat(p.getStatus()).isEqualTo(Status.PUBLISHED);
            }
        );

    }

    @Configuration
    @ComponentScan(basePackageClasses = JpaConfig.class)
    static class TestConfig {
    }

}
