package com.example.demo;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostClient {
    private final RestClient restClient;

    List<Post> allPosts() {
        return restClient.get().uri("/posts").accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    Post getById(UUID id) {
        var response = restClient.get().uri("/posts/{id}", id).accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus((HttpStatusCode s) -> s == HttpStatus.NOT_FOUND,
                        (HttpRequest req, ClientHttpResponse res) -> {
                            throw new PostNotFoundException(id);
                        }
                )
                .toEntity(Post.class);
        log.debug("response status code: {}", response.getStatusCode());
        return response.getBody();
    }

    void save(Post post) {
        var response = restClient.post().uri("/posts").contentType(MediaType.APPLICATION_JSON).body(post)
                .retrieve()
                .toBodilessEntity();

        log.debug("saved location:" + response.getHeaders().getLocation());
    }

    void update(UUID id, Post post) {
        var response = restClient.put().uri("/posts/{id}", id).contentType(MediaType.APPLICATION_JSON).body(post)
                .retrieve()
                .toBodilessEntity();

        log.debug("updated post status:" + response.getStatusCode());
    }

    void delete(UUID id) {
        var response = restClient.delete().uri("/posts/{id}", id).accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toBodilessEntity();

        log.debug("deleted post status:" + response.getStatusCode());
    }

}
