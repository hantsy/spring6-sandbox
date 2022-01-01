package com.example.demo.jdbc;

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
public class PostRepository {

    public static final RowMapper<Post> ROW_MAPPER = (rs, rowNum) -> new Post(
            rs.getObject("id", UUID.class),
            rs.getString("title"),
            rs.getString("content"),
            rs.getObject("status", Status.class),
            rs.getObject("created_at", LocalDateTime.class)
    );

    private final NamedParameterJdbcTemplate client;

    public List<Post> findByTitleContains(String name) {
        var sql = "SELECT * FROM posts WHERE title LIKE :title";
        return this.client.query(sql, Map.of("title", "%" + name + "%"), ROW_MAPPER);
    }

    public List<Post> findAll() {
        var sql = "SELECT * FROM posts";
        return this.client.query(sql, ROW_MAPPER);
    }

    public List<Map<String, Object>> countByStatus() {
        var sql = "SELECT count(*) as cnt, status FROM posts group by status";
        return this.client.queryForList(sql, Collections.emptyMap());
    }

    public Post findById(UUID id) {
        var sql = "SELECT * FROM posts WHERE id=:id";
        return this.client.queryForObject(sql, Map.of("id", id), ROW_MAPPER);
    }

    public UUID save(CreatePostCommand p) {
        var sql = "INSERT INTO  posts (title, content, status) VALUES (:title, :content, :status)";
        var keyHolder = new GeneratedKeyHolder();
        var paramSource = new MapSqlParameterSource(
                Map.of("title", p.title(), "content", p.content(), "status", Status.DRAFT)
        );
        var cnt = this.client.update(sql, paramSource, keyHolder, new String[]{"id"});
        return keyHolder.getKeyAs(UUID.class);
    }

    public int[] saveAll(List<Post> data) {
        var sql = "INSERT INTO  posts (title, content, status) VALUES (:title, :content, :status)";
        MapSqlParameterSource[] params = data.stream()
                .map(p -> new MapSqlParameterSource(Map.of("title", p.title(), "content", p.content(), "status", p.status())))
                .toList()
                .toArray(new MapSqlParameterSource[0]);
        return this.client.batchUpdate(sql, params);
    }

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

    public Integer deleteById(UUID id) {
        var sql = "DELETE FROM posts WHERE id=:id";
        return this.client.update(sql, Map.of("id", id));
    }

    public Integer deleteAll() {
        var sql = "DELETE FROM posts";
        return this.client.update(sql, Collections.emptyMap());
    }
}