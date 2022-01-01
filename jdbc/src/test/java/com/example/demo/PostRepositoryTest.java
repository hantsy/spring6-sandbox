package com.example.demo;

import com.example.demo.jdbc.CreatePostCommand;
import com.example.demo.jdbc.Post;
import com.example.demo.jdbc.PostRepository;
import com.example.demo.jdbc.Status;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author hantsy
 */
@Slf4j
@SpringJUnitConfig(classes = {DataSourceConfig.class, PostRepositoryTest.TestConfig.class})
public class PostRepositoryTest {

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

    @ComponentScan
    static class TestConfig {
    }

}
