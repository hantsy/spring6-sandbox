package com.example.demo.domain;

import com.example.demo.DataSourceConfig;
import com.example.demo.domain.model.CreatePostCommand;
import com.example.demo.domain.model.Post;
import com.example.demo.domain.model.Status;
import com.example.demo.domain.model.UpdatePostCommand;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

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
        var deleted = this.template.update("DELETE FROM posts");
        log.debug("deleted posts: {}", deleted);
    }

    @Test
    public void testSaveAndQuery() {
        var sqlInsert = "INSERT INTO posts(title, content) VALUES (?,?)";
        var inserted = this.template.execute(
                (Connection con) -> {
                    var ps = con.prepareStatement(sqlInsert);
                    ps.setString(1, "test");
                    ps.setString(2, "test content");
                    return ps;
                },
                PreparedStatement::executeUpdate
        );

        assertThat(inserted).isGreaterThan(0);

        var sqlQueryAll = "SELECT * FROM posts";
        var result = this.template.query(sqlQueryAll, (rs, rowNum) -> new Post(rs.getString("title"), rs.getString("content")));

        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).title()).isEqualTo("test");
        assertThat(result.get(0).content()).isEqualTo("test content");
    }

}
