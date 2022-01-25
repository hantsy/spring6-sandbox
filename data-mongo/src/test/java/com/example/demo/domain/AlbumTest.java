package com.example.demo.domain;

import com.example.demo.domain.model.Album;
import com.example.demo.domain.model.Status;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author hantsy
 */
@Slf4j
@SpringJUnitConfig(classes = {MongoConfig.class, DataMongoConfig.class, TestConfig.class})
public class AlbumTest {

    @Autowired
    MongoTemplate client;

    @SneakyThrows
    @BeforeEach
    public void setup() {
        this.client.dropCollection(Album.class);
    }

    @Test
    public void testSaveAll() {
        var data = List.of(Album.of("Java"), Album.of("Spring"));
        data.forEach(this.client::save);

        var results = this.client.findAll(Album.class);
        assertThat(results.size()).isEqualTo(2);
    }

    @Test
    public void testInsertAndQuery() {
        var name = "Spring Data Mongo";
        var data = Album.of(name);
        var saved = this.client.save(data);
        assertThat(saved.getId()).isNotNull();

        var album = this.client.findById(saved.getId(), Album.class);
        assertThat(album.getName()).isEqualTo(name);
    }


}
