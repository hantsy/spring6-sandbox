package com.example.demo.domain;

import com.example.demo.DataJpaConfig;
import com.example.demo.domain.model.Post;
import com.example.demo.domain.model.QPost;
import com.example.demo.domain.model.Status;
import com.example.demo.domain.repository.PostRepository;
import com.example.demo.domain.repository.Predicates;
import com.example.demo.domain.repository.Specifications;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author hantsy
 */
@Slf4j
@DataJpaTest
public class PostRepositoryTest {

    @TestConfiguration
    @Import(DataJpaConfig.class)
    static class TestConfig {
    }

    //@Inject
    @Autowired
    PostRepository posts;

    @BeforeEach
    public void setup() {
        this.posts.deleteAll();
    }

    @Test
    public void testSaveAll() {
        var data = List.of(
                Post.builder().title("test").content("data").status(Status.PENDING_MODERATION).build(),
                Post.builder().title("test1").content("content1").build());
        data.forEach(this.posts::save);

        var results = posts.findAll();
        assertThat(results.size()).isEqualTo(2);

        var resultsByKeyword = posts.findAll(Specifications.findByKeyword("test", Status.PENDING_MODERATION), PageRequest.of(0, 10));
        assertThat(resultsByKeyword.getTotalElements()).isEqualTo(1);

        var resultsByKeyword2 = posts.findAll(Specifications.findByKeyword("", null), PageRequest.of(0, 10));
        assertThat(resultsByKeyword2.getTotalElements()).isEqualTo(2);
    }

    @Test
    public void testSaveAllAndFindAll_QueryDSL() {

        var data = List.of(
                Post.builder().title("test").content("data").status(Status.PENDING_MODERATION).build(),
                Post.builder().title("test1").content("content1").build());
        data.forEach(this.posts::save);

        var results = posts.findAll();
        assertThat(results.size()).isEqualTo(2);

        var resultsByKeyword = posts.findAll(Predicates.findByKeyword("test", Status.PENDING_MODERATION), PageRequest.of(0, 10));
        assertThat(resultsByKeyword.getTotalElements()).isEqualTo(1);

        var resultsByKeyword2 = posts.findAll(Predicates.findByKeyword("", null), PageRequest.of(0, 10));
        assertThat(resultsByKeyword2.getTotalElements()).isEqualTo(2);
    }

    @Test
    public void testInsertAndQuery() {
        var data = Post.builder().title("test").content("test data").status(Status.DRAFT).build();
        var saved = this.posts.save(data);
        this.posts.findById(saved.getId()).ifPresent(
                p -> {
                    assertThat(p.getStatus()).isEqualTo(Status.DRAFT);

                    //assert Spring Data JPA Auditing
                    assertThat(p.getCreatedAt()).isNotNull();
                    assertThat(p.getCreatedBy()).isNotNull();
                }
        );
    }

    @Test
    public void testInsertAndQuery_QueryDSL() {
        var data = Post.builder().title("test").content("test data").status(Status.DRAFT).build();
        var saved = this.posts.save(data);
        this.posts.findOne(QPost.post.id.eq(saved.getId())).ifPresent(
                p -> assertThat(p.getStatus()).isEqualTo(Status.DRAFT)
        );
    }

    @Test
    public void testInsertAndQuery_QueryByExample() {
        var data = Post.builder().title("test").content("test data").status(Status.DRAFT).build();
        var saved = this.posts.save(data);
        var probe = Post.builder().id(saved.getId()).build();
        this.posts.findOne(Example.of(probe, ExampleMatcher.matching().withIgnorePaths("status"))).ifPresent(
                p -> assertThat(p.getStatus()).isEqualTo(Status.DRAFT)
        );
    }

    @Test
    public void testLabels() {
        var data = List.of(
                Post.builder().title("test").content("data").labels(Set.of("java17", "spring6")).build(),
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

        var results5 = (List<Post>) posts.findAll(QPost.post.labels.contains("java17"));
        assertThat(results5.size()).isEqualTo(1);
    }
}
