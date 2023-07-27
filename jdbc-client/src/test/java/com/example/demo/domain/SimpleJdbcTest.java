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
import org.springframework.jdbc.object.BatchSqlUpdate;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.MappingSqlQueryWithParameters;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
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
        var deleted = new DeleteAll(this.dataSource).update();
        log.debug("deleted posts: {}", deleted);
    }

    public void testSaveAllAndQuery() {
        var data = List.of(
                Post.of("test", "test content"),
                Post.of("test 1", "test content 1")
        );
        var savedCount = new SaveAll(this.dataSource).go(data);
        assertThat(savedCount.length).isEqualTo(2);

        var allPosts = new FindAll(dataSource).execute();
        assertThat(allPosts).isNotNull();
        assertThat(allPosts).isNotEmpty();
        assertThat(allPosts.size()).isEqualTo(2);
    }

    @Test
    public void testSaveAndQuery() {
        var insertedId = new Save(dataSource).go("test", "test content");
        assertThat(insertedId).isNotNull();

        var result = new FindById(dataSource).findObject(insertedId);
        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("test");
        assertThat(result.content()).isEqualTo("test content");
    }

}

class DeleteAll extends SqlUpdate {
    final String sqlDelAllPosts = "DELETE FROM posts";

    public DeleteAll(DataSource dataSource) {
        setDataSource(dataSource);
        setSql(sqlDelAllPosts);
        compile();
    }
}

class Save {

    SimpleJdbcInsert simpleJdbcInsert;

    public Save(DataSource dataSource) {
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

class SaveAll extends BatchSqlUpdate {
    public SaveAll(DataSource dataSource) {
        super(dataSource, "INSERT INTO posts(title, content) VALUES (?, ?)");
        declareParameter(new SqlParameter("title", Types.VARCHAR));
        declareParameter(new SqlParameter("content", Types.VARCHAR));
        compile();
    }

    public int[] go(List<Post> posts) {
        posts.forEach(p -> this.update(p.title(), p.content()));
        return this.flush();
    }
}

class FindById extends MappingSqlQueryWithParameters<Post> {

    public FindById(DataSource ds) {
        super(ds, "SELECT * FROM posts WHERE id = ?");
        declareParameter(new SqlParameter("id", Types.OTHER));
        compile();
    }

    @Override
    protected Post mapRow(ResultSet rs, int rowNum, Object[] parameters, Map<?, ?> context) throws SQLException {
        return Post.of(rs.getString("title"), rs.getString("content"));
    }
}

class FindAll extends MappingSqlQuery<Post> {

    public FindAll(DataSource ds) {
        super(ds, "SELECT * FROM posts");
        compile();
    }

    @Override
    protected Post mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Post.of(rs.getString("title"), rs.getString("content"));
    }
}