package com.example.demo.domain;

import com.example.demo.DataSourceConfig;
import com.example.demo.domain.model.Post;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.MappingSqlQueryWithParameters;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author hantsy
 */
@Slf4j
@SpringJUnitConfig(classes = {DataSourceConfig.class, JdbcConfig.class, TestConfig.class})
public class SimpleJdbcTest {

    @Autowired
    DataSource dataSource;

    @SneakyThrows
    @BeforeEach
    public void setup() {
        var deleted = new DeleteAllPosts(this.dataSource).go();
        log.debug("deleted posts: {}", deleted);
    }

    @Test
    public void testSaveAndQuery() {

        var insertedId = new InsertNewPost(dataSource).go("test", "test content");

        assertThat(insertedId).isNotNull();

        var allPosts = new FindAllPosts(dataSource).execute();

        assertThat(allPosts).isNotNull();
        assertThat(allPosts).isNotEmpty();

        assertThat(allPosts.get(0).title()).isEqualTo("test");
        assertThat(allPosts.get(0).content()).isEqualTo("test content");

        var result = new FindPostById(dataSource).findObject(insertedId);

        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("test");
        assertThat(result.content()).isEqualTo("test content");
    }

}

class DeleteAllPosts {
    final String sqlDelAllPosts = "DELETE FROM posts";
    SqlUpdate sqlUpdate;

    public DeleteAllPosts(DataSource dataSource) {
        this.sqlUpdate = new SqlUpdate(dataSource, sqlDelAllPosts);
    }

    public int go() {
        return this.sqlUpdate.update();
    }
}

class InsertNewPost {

    SimpleJdbcInsert simpleJdbcInsert;

    public InsertNewPost(DataSource dataSource) {
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("posts")
                .usingColumns("title", "content")
                .usingGeneratedKeyColumns("id");
    }

    public UUID go(String title, String content) {
        var keyHolder = this.simpleJdbcInsert.executeAndReturnKeyHolder(Map.of(
                "title", title,
                "content", content
        ));
        return (UUID) keyHolder.getKeys().get("id");
    }
}

class FindPostById extends MappingSqlQueryWithParameters<Post> {

    public FindPostById(DataSource ds) {
        super(ds, "SELECT * FROM posts WHERE id = ?");
        declareParameter(new SqlParameter("id", Types.OTHER));
        compile();
    }

    @Override
    protected Post mapRow(ResultSet rs, int rowNum, Object[] parameters, Map<?, ?> context) throws SQLException {
        return new Post(rs.getString("title"), rs.getString("content"));
    }
}

class FindAllPosts extends MappingSqlQuery<Post> {

    public FindAllPosts(DataSource ds) {
        super(ds, "SELECT * FROM posts");
        compile();
    }

    @Override
    protected Post mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Post(rs.getString("title"), rs.getString("content"));
    }
}