package com.example.demo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer {

    private final DatabaseClient databaseClient;

    @EventListener(value = ContextRefreshedEvent.class)
    public void init() throws Exception {
        log.info("start data initialization...");
        var insertSql = """
                INSERT INTO  posts (title, content)
                VALUES (:title, :content)
                """;
        this.databaseClient
                .sql(insertSql)
                .filter((statement, __) -> statement.returnGeneratedValues("id").execute())
                .bind("title", "Spring 6 and R2dbc")
                .bind("content", "content of my Spring 6 reactive post")
                .fetch()
                .first()
                .subscribe(
                        data -> log.info("inserted data : {}", data),
                        error -> log.info("error: {}", error)
                );

    }
}