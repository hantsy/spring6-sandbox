package com.example.demo.domain;

import com.example.demo.DataSourceConfig;
import com.example.demo.domain.model.Post;
import com.example.demo.domain.model.Status;
import com.example.demo.domain.repository.PostRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author hantsy
 */
@Slf4j
@SpringJUnitConfig(classes = {DataSourceConfig.class, JdbcConfig.class, TestConfig.class})
//@ContextConfiguration(initializers = PostRepositoryTestWithTestcontainers.TestContainerInitializer.class)
public class PostRepositoryTest {

//see: https://github.com/testcontainers/testcontainers-java/discussions/4841
//    static class TestContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
//
//        @Override
//        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
//            var container = new PostgreSQLContainer("postgres:12")
//                    .withInitScript("init.sql");
//            container.start();
//            log.info(" container.getFirstMappedPort():: {}", container.getFirstMappedPort());
//            configurableApplicationContext
//                    .addApplicationListener((ApplicationListener<ContextClosedEvent>) event -> container.stop());
//            var env = configurableApplicationContext.getEnvironment();
//            var props = env.getPropertySources();
//            props.addFirst(
//                    new MapPropertySource("testdatasource",
//                            Map.of("datasource.url", container.getJdbcUrl(),
//                                    "datasource.username", container.getUsername(),
//                                    "datasource.password", container.getPassword()
//                            )
//                    )
//            );
//        }
//    }

    @Autowired
    PostRepository posts;

    @SneakyThrows
    @BeforeEach
    public void setup() {
        this.posts.deleteAll();
    }

    @Test
    public void testSaveAll() {

        var data = Post.builder().title("test").content("content").status(Status.PENDING_MODERATION).build();
        var data1 = Post.builder().title("test1").content("content1").build();

        var result = (List) posts.saveAll(List.of(data, data1));

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);

        var countByStatus = posts.countByStatus();

        assertThat(countByStatus.get(0).get("status")).isEqualTo(Status.DRAFT.name());
        var item2 = countByStatus.get(1);
        assertThat(item2.get("cnt")).isEqualTo(1L);
        assertThat(item2.get("status")).isEqualTo(Status.PENDING_MODERATION.name());

    }

    @Test
    public void testInsertAndQuery() {
        var data = Post.builder().title("test").content("content").build();
        var post = this.posts.save(data);
        var saved = this.posts.findById(post.getId());

        saved.ifPresent(p -> {
            assertThat(p.getStatus()).isEqualTo(Status.DRAFT);
        });

        saved.map(p -> {
                            p.setTitle("updated test");
                            p.setContent("updated content");
                            p.setStatus(Status.PENDING_MODERATION);

                            return p;
                        }
                )
                .map(p -> this.posts.save(p))
                .flatMap(p -> this.posts.findById(p.getId()))
                .ifPresent(p -> {
                    assertThat(p.getStatus()).isEqualTo(Status.PENDING_MODERATION);
                });
    }

}
