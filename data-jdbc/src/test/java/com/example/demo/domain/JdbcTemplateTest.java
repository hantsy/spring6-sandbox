package com.example.demo.domain;

import com.example.demo.DataSourceConfig;
import com.example.demo.domain.model.Post;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author hantsy
 */
@Slf4j
@SpringJUnitConfig(classes = {DataSourceConfig.class, JdbcConfig.class, TestConfig.class})
public class JdbcTemplateTest {

    @Autowired
    JdbcTemplate template;

    @SneakyThrows
    @BeforeEach
    public void setup() {
        var deletedLabels = this.template.update("DELETE FROM post_labels");
        var deleted = this.template.update("DELETE FROM posts");
        var deletedUsers = this.template.update("DELETE FROM users");
        log.debug("deleted posts: {}, labels: {}, users: {}", deleted, deletedLabels, deletedUsers);
    }

    @Test
    public void testSaveAllAndQuery() {
        var sqlInsert = "INSERT INTO posts(title, content) VALUES (?,?)";
        var data = List.of(
                Post.builder().title("test").content("content of test").build(),
                Post.builder().title("test 2").content("content of test 2").build()
        );
        var inserted = this.template.batchUpdate(
                sqlInsert,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, data.get(i).getTitle());
                        ps.setString(2, data.get(i).getContent());
                    }

                    @Override
                    public int getBatchSize() {
                        return data.size();
                    }
                }
        );

        assertThat(inserted.length).isEqualTo(2);

        var sqlQueryAll = "SELECT * FROM posts";
        var result = this.template.query(sqlQueryAll, (rs, rowNum) -> Post.builder().title(rs.getString("title")).content(rs.getString("content")).build());
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void testSaveAndQuery() {
        var sqlInsert = "INSERT INTO posts(title, content) VALUES (?,?)";
        var keyHolder = new GeneratedKeyHolder();
        var inserted = this.template.update(
                (Connection con) -> {
                    var ps = con.prepareStatement(sqlInsert, new String[]{"id"});
                    ps.setString(1, "test");
                    ps.setString(2, "test content");
                    return ps;
                },
                keyHolder
        );

        assertThat(inserted).isGreaterThan(0);
        UUID id = (UUID) keyHolder.getKeys().get("id");

        var sqlQuery = "SELECT * FROM posts WHERE id=?";
        var result = this.template.queryForObject(
                sqlQuery,
                (rs, rowNum) -> Post.builder().title(rs.getString("title")).content(rs.getString("content")).build(),
                id);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("test");
        assertThat(result.getContent()).isEqualTo("test content");
    }

}
