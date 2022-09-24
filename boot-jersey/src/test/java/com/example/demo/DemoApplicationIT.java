package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DemoApplicationIT {

    TestRestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName(" GET '/' should return status 200")
    void getAllPosts() {
        var resEntity = restTemplate.getForEntity("http://localhost:8080/posts", Post[].class);
        assertThat(resEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        var posts = resEntity.getBody();
        assertThat(posts.length).isEqualTo(1);
        assertThat(posts[0].getTitle()).contains("Tomcat");
    }

}
