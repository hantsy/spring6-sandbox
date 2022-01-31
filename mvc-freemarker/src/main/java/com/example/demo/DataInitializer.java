package com.example.demo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collections;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer {

    private final NamedParameterJdbcTemplate template;
    private final TransactionTemplate tx;

    @EventListener(value = ContextRefreshedEvent.class)
    public void init() throws Exception {
        log.info("start data initialization...");
        tx.executeWithoutResult(status -> {
            var delSql = "DELETE FROM posts";
            var deleted = this.template.update(delSql, Collections.emptyMap());
            log.debug("deleted rows : {}", deleted);
            var sql = "INSERT INTO  posts (title, content) VALUES (:title, :content)";
            var params = Map.of("title", "My first Spring 6 post", "content", "content of my Spring 6 reactive post");
            var inserted = this.template.update(sql, params);
            if (inserted > 0) {
                log.debug("inserted rows : {}", inserted);
            }
        });
    }
}