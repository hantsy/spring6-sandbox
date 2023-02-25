package com.example.demo.testcontainers;

import com.example.demo.DataSourceConfig;
import com.example.demo.domain.DataJpaConfig;
import com.example.demo.domain.JpaConfig;
import com.example.demo.domain.model.Address;
import com.example.demo.domain.model.Author;
import com.example.demo.domain.model.Post;
import com.example.demo.domain.model.Status;
import com.example.demo.domain.repository.AuthorRepository;
import com.example.demo.domain.repository.PostRepository;
import com.example.demo.domain.repository.Specifications;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author hantsy
 */
@Slf4j
@SpringJUnitConfig(classes = {DataSourceConfig.class, JpaConfig.class, DataJpaConfig.class, TestConfig.class})
@ContextConfiguration(initializers = TestContainerInitializer.class)
public class AuthorRepositoryTest {

    @Autowired
    AuthorRepository authorRepository;

    @SneakyThrows
    @BeforeEach
    public void setup() {
        log.debug("setup tests, clear data ...");
        this.authorRepository.deleteAll();
    }

    @Test
    public void testInsertAndQuery() {
        var data = Author.builder().name("Test").email("test@example.com")
            .address(new Address("street", "BJ", "102242"));

        var saved = this.authorRepository.save(data);
        this.authorRepository.findById(saved.getId()).ifPresent(
            p -> assertThat(p.getAddress().postalCode()).isEqualTo(102242)
        );

    }

}
