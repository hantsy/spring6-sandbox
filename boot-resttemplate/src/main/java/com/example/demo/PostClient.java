package com.example.demo;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostClient {
    private final RestTemplate template;

    List<Post> allPosts() {
        var headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE) ;
        var entity = new HttpEntity<>(headers);
        return template.exchange("/posts",
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<List<Post>>() {
                        }
                )
                .getBody();
    }

    Post getById(UUID id) {
        var response = template.getForEntity("/posts/{id}", Post.class, id);
        if (response.getStatusCode() == HttpStatusCode.valueOf(404)) return null;
        return response.getBody();
    }

    void save(Post post) {
        var location = template.postForLocation("/posts", post);
        log.debug("saved location:" + location);
    }

    void update(UUID id, Post post) {
        template.put("/posts/{id}", post, id);
    }

    void delete(UUID id) {
        template.delete("/posts/{id}", id);
    }

}
