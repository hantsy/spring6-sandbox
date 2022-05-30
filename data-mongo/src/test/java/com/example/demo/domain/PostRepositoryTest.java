package com.example.demo.domain;

import com.example.demo.domain.model.Post;
import com.example.demo.domain.model.QPost;
import com.example.demo.domain.model.Status;
import com.example.demo.domain.repository.PostRepository;
import com.example.demo.domain.repository.Predicates;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Set;

import static com.mongodb.assertions.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.domain.ExampleMatcher.matching;

/**
 * @author hantsy
 */
@Slf4j
@SpringJUnitConfig(classes = {MongoConfig.class, DataMongoConfig.class, TestConfig.class})
public class PostRepositoryTest {

    //@Inject
    @Autowired
    PostRepository posts;

    @SneakyThrows
    @BeforeEach
    public void setup() {
        this.posts.deleteAll();
    }

    @Test
    public void testSaveAll() {

        var data = List.of(
                Post.builder().title("test").content("content").status(Status.PENDING_MODERATION).build(),
                Post.builder().title("test1").content("content1").build());
        data.forEach(this.posts::save);

        var results = posts.findAll();
        assertThat(results.size()).isEqualTo(2);
    }

    @Test
    @Disabled
    public void testSaveAllAndFindAll_QueryDSL() {

        var data = List.of(
                Post.builder().title("test").content("content").status(Status.PENDING_MODERATION).build(),
                Post.builder().title("test1").content("content1").build());
        data.forEach(this.posts::save);

        var results = posts.findAll();
        assertThat(results.size()).isEqualTo(2);

        var resultsByKeyword1 = posts.findAll(Predicates.findByKeyword("test123", Status.PENDING_MODERATION), PageRequest.of(0, 10));
        assertThat(resultsByKeyword1.getTotalElements()).isEqualTo(0);

        // see: https://stackoverflow.com/questions/70848524/spring-data-mongo-querydsl-like-predicate-does-not-work-as-expected
        var resultsByKeyword2 = posts.findAll(Predicates.findByKeyword("test", Status.PENDING_MODERATION), PageRequest.of(0, 10));
        assertThat(resultsByKeyword2.getTotalElements()).isEqualTo(1);

        var resultsByKeyword3 = posts.findAll(Predicates.findByKeyword("", Status.PENDING_MODERATION), PageRequest.of(0, 10));
        assertThat(resultsByKeyword3.getTotalElements()).isEqualTo(1);

        var resultsByKeyword4 = posts.findAll(Predicates.findByKeyword("", null), PageRequest.of(0, 10));
        assertThat(resultsByKeyword4.getTotalElements()).isEqualTo(2);
    }

    @Test
    public void testInsertAndQuery() {
        var data = Post.builder().title("test").content("test content").status(Status.DRAFT).build();
        var saved = this.posts.save(data);
        var post = this.posts.findById(saved.getId());
        assertTrue(post.isPresent());
        post.ifPresent(
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
        var data = Post.builder().title("test").content("test content").status(Status.DRAFT).build();
        var saved = this.posts.save(data);
        var post = this.posts.findOne(QPost.post.id.eq(saved.getId()));
        assertTrue(post.isPresent());
        post.ifPresent(
                p -> assertThat(p.getStatus()).isEqualTo(Status.DRAFT)
        );
    }

    @Test
    public void testInsertAndQuery_QueryByExample() {
        var data = Post.builder().title("test").content("test content").status(Status.DRAFT).build();
        var saved = this.posts.save(data);
        var probe = Post.builder().title("test").build();
        var matcher = matching().withIgnorePaths("status", "slug")
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        var post = this.posts.findOne(Example.of(probe, matcher));
        assertTrue(post.isPresent());
        post.ifPresent(
                p -> assertThat(p.getStatus()).isEqualTo(Status.DRAFT)
        );
    }

    @Test
    public void testLabels() {
        var data = List.of(
                Post.builder().title("test").content("content").labels(Set.of("java17", "spring6")).build(),
                Post.builder().title("test1").content("content1").labels(Set.of("spring6")).build());
        this.posts.saveAll(data).forEach(saved -> log.debug("saved: {}", saved));

        var results = posts.findPostByLabels("spring");
        assertThat(results.size()).isEqualTo(0);

        var results2 = posts.findPostByLabels("spring6");
        assertThat(results2.size()).isEqualTo(2);

        var results3 = posts.findPostByLabels("java17");
        assertThat(results3.size()).isEqualTo(1);

        var results4 = posts.findPostByLabels("java17", "spring", "spring6");
        assertThat(results4.size()).isEqualTo(2);

        var results5 = (List<Post>) posts.findAll(QPost.post.labels.contains("java17"));
        assertThat(results5.size()).isEqualTo(1);
    }

}
