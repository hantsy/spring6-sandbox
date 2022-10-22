package com.example.demo.domain;

import com.example.demo.domain.model.Post;
import com.example.demo.domain.model.Status;
import com.example.demo.domain.repository.PostRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringJUnitConfig(classes = {JpaConfig.class, DataJpaConfig.class, PostRepositoryTest.TestConfig.class})
public class PostRepositoryTest {

    //@Inject
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
        var data = List.of(
                Post.builder().title("test").content("content").status(Status.PENDING_MODERATION).build(),
                Post.builder().title("test1").content("content1").build());
        tx.executeWithoutResult(__ -> data.forEach(this.posts::save));

        posts.readAllBy()
                .thenAcceptAsync(r -> assertThat(r.size()).isEqualTo(2))
                .join();
    }


    @Configuration
    @ComponentScan
    static class TestConfig {

        @Bean
        @Primary
        public DataSource embeddedDataSource() {
            return new EmbeddedDatabaseBuilder()
                    .setType(EmbeddedDatabaseType.H2)
                    .build();
        }
    }

}
