package com.example.demo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
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

    private final JdbcClient client;

    @Override
    public List<Post> findByTitleContains(String name) {
        var sql = "SELECT * FROM posts WHERE title LIKE :title";
        return this.client.sql(sql)
                .params(Map.of("title", "%" + name + "%"))
                .query(ROW_MAPPER)
                .list();
    }

    @Override
    public List<Post> findAll() {
        var sql = "SELECT * FROM posts";
        return this.client.sql(sql).query(ROW_MAPPER).list();
    }

    @Override
    public Map<Status, Long> countByStatus() {
        var sql = "SELECT count(*) as cnt, status FROM posts group by status";
        List<Map<String, Object>> listOfRows = this.client.sql(sql).query().listOfRows();
        log.debug("list of status count pair:{}", listOfRows);
        return listOfRows
                .stream()
                .collect(
                        HashMap::new,
                        (statusLongHashMap, stringObjectMap) -> {
                            log.debug("input of accumulator: {}, {}", statusLongHashMap, stringObjectMap);
                            statusLongHashMap.put(
                                    Status.valueOf((stringObjectMap.get("status")).toString()),
                                    Long.valueOf((stringObjectMap.get("cnt")).toString())
                            );
                        },
                        (statusLongHashMap, stringObjectMap) -> {
                            log.debug("input of combiner: {}, {}", statusLongHashMap, stringObjectMap);
                        }
                );
    }

    @Override
    public Post findById(UUID id) {
        var sql = "SELECT * FROM posts WHERE id=:id";
        return this.client.sql(sql).params(Map.of("id", id)).query(ROW_MAPPER).single();
    }

    @Override
    public UUID save(Post p) {
        var sql = """
                INSERT INTO  posts (title, content, status) 
                VALUES (:title, :content, CAST(:status as post_status)) 
                RETURNING id
                """;
        var keyHolder = new GeneratedKeyHolder();
        var paramSource = new MapSqlParameterSource(
                Map.of("title", p.title(), "content", p.content(), "status", p.status().name())
        );
        var cnt = this.client.sql(sql).paramSource(paramSource).update(keyHolder);
        log.debug("updated count:{}", cnt);
        return keyHolder.getKeyAs(UUID.class);
    }

    @Override
    public Integer update(Post p) {
        var sql = "UPDATE posts set title=:title, content=:content, status=CAST(:status as post_status) WHERE id=:id";
        Map<String, ? extends Serializable> params = Map.of(
                "title", p.title(),
                "content", p.content(),
                "status", p.status().name(),
                "id", p.id()

        );
        return this.client.sql(sql).params(params).update();
    }

    @Override
    public Integer deleteById(UUID id) {
        var sql = "DELETE FROM posts WHERE id=:id";
        return this.client.sql(sql).params(Map.of("id", id)).update();
    }

    @Override
    public Integer deleteAll() {
        var sql = "DELETE FROM posts";
        return this.client.sql(sql).update();
    }

    @Override
    public Long count() {
        var sql = "SELECT count(*) FROM posts";
        var count = this.client.sql(sql).query().singleValue();
        log.debug("count is: {}", count);
        return (Long)count;
    }
}