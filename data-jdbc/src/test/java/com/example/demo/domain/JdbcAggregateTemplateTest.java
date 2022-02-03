package com.example.demo.domain;

import com.example.demo.DataSourceConfig;
import com.example.demo.domain.model.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author hantsy
 */
@Slf4j
@SpringJUnitConfig(classes = {DataSourceConfig.class, JdbcConfig.class, TestConfig.class})
public class JdbcAggregateTemplateTest {

    @Autowired
    JdbcAggregateTemplate template;

    @Autowired
    TransactionTemplate tx;

    @SneakyThrows
    @BeforeEach
    public void setup() {
        this.template.deleteAll(Label.class);
        this.template.deleteAll(Post.class);
        this.template.deleteAll(User.class);

        this.template.deleteAll(VersionedPost.class);
        this.template.deleteAll(PersistablePost.class);
        this.template.deleteAll(PopularPost.class);
    }

    @Test
    public void givenEntityWithVersion_whenSetVersionValue_andSave_thenExecuteUpdateActionAndThrowsException() {
        var data = new VersionedPost();
        data.setTitle("test");
        data.setContent("test content");
        data.setVersion(1L);
        // the version is set, `save` method will execute a `update` action.
        assertThatThrownBy(() -> this.template.save(data)).isInstanceOf(DbActionExecutionException.class);
    }

    @Test
    public void givenEntityWithVersion_whenSetVersionValue_andInsert_thenIgnoreVersionAndInsertCorrectly() {
        var data = new VersionedPost();
        data.setTitle("test");
        data.setContent("test content");
        data.setVersion(1000L);
        // the `insert` will insert a new record into the database by force
        var inserted = this.template.insert(data);
        assertThat(inserted.getId()).isNotNull();
        assertThat(inserted.getVersion()).isNotEqualTo(1000L);
    }

    @Test
    public void givenEntityImplementsPersistableInterface_whenIsNewReturnTrue_andSave_thenExecuteUpdateActionAndThrowsException() {
        var data = new PersistablePost();
        data.setTitle("test");
        data.setContent("test content");
        data.setId(UUID.randomUUID());
        // the id is set, then `isNew` returns a `true`, `save` method will execute a `update` action.
        assertThatThrownBy(() -> this.template.save(data)).isInstanceOf(DbActionExecutionException.class);
    }

    @Test
    public void givenEntityImplementsPersistableInterface_whenIsNewReturnTrue_andInsert_thenIgnoreIsNewCheckAndInsertCorrectly() {
        var data = new PersistablePost();
        data.setTitle("test");
        data.setContent("test content");
        var id = UUID.randomUUID();
        data.setId(id);

        // the `insert` will insert a new record into the database by force
        var inserted = this.template.insert(data);
        assertThat(inserted.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("test saving record entity")
    public void testInsertPopularPosts() {
        var data = new PopularPost(null, "test", "test content", LocalDateTime.now(), null);
        var inserted = this.template.insert(data);
        assertThat(inserted.id()).isNotNull();
        assertThat(inserted.title()).isEqualTo("test");
        assertThat(inserted.content()).isEqualTo("test content");
        assertThat(inserted.createdAt()).isNotNull();
        assertThat(inserted.version()).isGreaterThanOrEqualTo(0L);

        var existed = this.template.findById(inserted.id(), PopularPost.class);
        assertThat(existed.title()).isEqualTo("test");
        assertThat(existed.content()).isEqualTo("test content");

        var updated = this.template.update(new PopularPost(existed.id(), "updated test", "updated content", existed.createdAt(), existed.version()));
        assertThat(updated.title()).isEqualTo("updated test");
        assertThat(updated.content()).isEqualTo("updated content");
        assertThat(updated.version()).isGreaterThanOrEqualTo(1L);
    }


    @Test
    public void whenSavePostList_thenFindAll() {
        var data = Post.builder().title("test").content("content").status(Status.PENDING_MODERATION).build();
        var data1 = Post.builder().title("test1").content("content1").build();
        this.template.save(data);
        this.template.save(data1);

        var posts = this.template.findAll(Post.class);
        assertThat(StreamSupport.stream(posts.spliterator(), false).map(Post::getTitle))
                .containsExactlyInAnyOrder("test", "test1");
    }

    @Test
    public void whenSavePostWithLabelAndExternalRef_thenFindByIdGetAllRelations() {
        var user = User.of("testuesr", "test@example.com");
        var savedUser = tx.execute(__ -> this.template.save(user));
        log.debug("saved user: {}", savedUser);

        var post = Post.builder()
                .title("test")
                .content("test content")
                .moderator(AggregateReference.to(savedUser.getId()))
                .build();
        post.addLabel("Spring");
        post.addLabel("Spring Data Jdbc");

        var savedPost = tx.execute(__ -> this.template.save(post));
        log.debug("saved post: {}", savedPost);

        var foundPost = tx.execute(__ -> this.template.findById(savedPost.getId(), Post.class));
        log.debug("found post by id: {}", foundPost);
        assertThat(foundPost.getTitle()).isEqualTo("test");
        assertThat(foundPost.getContent()).isEqualTo("test content");
        assertThat(foundPost.getCreatedAt()).isNotNull();
        assertThat(foundPost.getLabels().size()).isEqualTo(2);
        assertThat(foundPost.getModerator().getId()).isEqualTo(savedUser.getId());
    }

}
