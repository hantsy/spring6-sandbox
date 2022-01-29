package com.example.demo.domain;

import com.example.demo.Post;
import com.example.demo.PostRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.stream.IntStream;

@Slf4j
@SpringJUnitConfig(classes = {TestConfig.class})
public class PostRepositoryTest {

    @Autowired
    PostRepository posts;

    @Autowired
    TransactionTemplate tx;

    @SneakyThrows
    @BeforeEach
    public void setup() {
        this.posts.deleteAll();
    }

    @Test
    public void testSaveAll() {
        var data = Post.of("test", "content");
        var saved = tx.execute(__ -> this.posts.save(data));

        IntStream.range(1, 11)
                .forEach(i -> {
                    log.debug(" find by id #{} ", i);
                    this.posts.findById(saved.getId());
                });

        saved.setTitle("updated title");
        var updated = tx.execute(__ -> this.posts.save(saved));
        IntStream.range(11, 21)
                .forEach(i -> {
                    log.debug(" find by id #{} ", i);
                    this.posts.findById(updated.getId());
                });
    }

}
