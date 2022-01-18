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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.sql.DataSource;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author hantsy
 */
@Slf4j
@SpringJUnitConfig(classes = {JpaConfig.class, DataJpaConfig.class, PostRepositoryTest.TestConfig.class})
public class PostRepositoryTest {

    //@Inject
    @Autowired
    PostRepository posts;

    @SneakyThrows
    @BeforeEach
    public void setup() {
        var deleted = this.posts.customDeleteAll();
        log.debug("Deleted posts: {}", deleted);
    }

    @Test
    public void testSaveAll() {

        var data = List.of(
                Post.builder().title("test").content("content").status(Status.PENDING_MODERATION).build(),
                Post.builder().title("test1").content("content1").build());
        data.forEach(this.posts::save);

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
    public void testLabels() {
        var data = List.of(
                Post.builder().title("test").content("content").labels(Set.of("java17", "spring6")).build(),
                Post.builder().title("test1").content("content1").labels(Set.of("spring6")).build());
        data.forEach(this.posts::save);

        var results = posts.findPostByLabels("spring");
        assertThat(results.size()).isEqualTo(0);

        var results2 = posts.findPostByLabels("spring6");
        assertThat(results2.size()).isEqualTo(2);

        var results3 = posts.findPostByLabels("java17");
        assertThat(results3.size()).isEqualTo(1);

        var results4 = posts.findPostByLabels("java17", "spring6");
        assertThat(results4.size()).isEqualTo(2);
    }

    @Configuration
    @ComponentScan
    static class TestConfig {

        @Bean
        @Primary
        public DataSource embeddedDataSource() {
            return new EmbeddedDatabaseBuilder()
                    .setType(EmbeddedDatabaseType.H2)
                    .build();
        }
    }

}
