package com.example.demo.domain.repository;

import com.example.demo.domain.model.CreatePostCommand;
import com.example.demo.domain.model.Post;
import com.example.demo.domain.model.Status;
import com.example.demo.domain.model.UpdatePostCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Repository
@Transactional
public class JdbcPostRepository implements PostRepository {

    public static final RowMapper<Post> ROW_MAPPER = (rs, rowNum) -> new Post(
            rs.getObject("id", UUID.class),
            rs.getString("title"),
            rs.getString("content"),
            //see: https://github.com/pgjdbc/pgjdbc/issues/2387
            //rs.getObject("status", Status.class),
            Status.valueOf(rs.getString("status")),
            rs.getObject("created_at", LocalDateTime.class)
    );

    private final NamedParameterJdbcTemplate client;

    @Override
    public List<Post> findByTitleContains(String name) {
        var sql = "SELECT * FROM posts WHERE title LIKE :title";
        return this.client.query(sql, Map.of("title", "%" + name + "%"), ROW_MAPPER);
    }

    @Override
    public List<Post> findAll() {
        var sql = "SELECT * FROM posts";
        return this.client.query(sql, ROW_MAPPER);
    }

    @Override
    public List<Map<String, Object>> countByStatus() {
        var sql = "SELECT count(*) as cnt, status FROM posts group by status";
        return this.client.queryForList(sql, Collections.emptyMap());
    }

    @Override
    public Post findById(UUID id) {
        var sql = "SELECT * FROM posts WHERE id=:id";
        return this.client.queryForObject(sql, Map.of("id", id), ROW_MAPPER);
    }

    @Override
    public UUID save(CreatePostCommand p) {
        var sql = "INSERT INTO  posts (title, content, status) VALUES (:title, :content, :status)";
        var keyHolder = new GeneratedKeyHolder();
        var paramSource = new MapSqlParameterSource(
                Map.of("title", p.title(), "content", p.content(), "status", Status.DRAFT)
        );
        var cnt = this.client.update(sql, paramSource, keyHolder, new String[]{"id"});
        return keyHolder.getKeyAs(UUID.class);
    }

    @Override
    public int[] saveAll(List<Post> data) {
        var sql = "INSERT INTO  posts (title, content, status) VALUES (:title, :content, :status)";
        MapSqlParameterSource[] params = data.stream()
                .map(p -> new MapSqlParameterSource(Map.of("title", p.title(), "content", p.content(), "status", p.status())))
                .toList()
                .toArray(new MapSqlParameterSource[0]);
        return this.client.batchUpdate(sql, params);
    }

    @Override
    public Integer update(UUID id, UpdatePostCommand p) {
        var sql = "UPDATE posts set title=:title, content=:content, status=:status WHERE id=:id";
        Map<String, ? extends Serializable> params = Map.of(
                "title", p.title(),
                "content", p.content(),
                "status", p.status(),
                "id", id

        );
        return this.client.update(sql, params);
    }

    @Override
    public Integer deleteById(UUID id) {
        var sql = "DELETE FROM posts WHERE id=:id";
        return this.client.update(sql, Map.of("id", id));
    }

    @Override
    public Integer deleteAll() {
        var sql = "DELETE FROM posts";
        return this.client.update(sql, Collections.emptyMap());
    }
}