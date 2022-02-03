package com.example.demo.web;

import com.example.demo.domain.model.Post;
import com.example.demo.domain.repository.PostRepository;
import com.example.demo.service.PostCreated;
import com.example.demo.service.PostEventPublisher;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.created;

@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
@Validated//does not work on record.
public class PostController {
    private final PostRepository posts;
    private final PostEventPublisher eventPublisher;

    @GetMapping(value = "", produces = APPLICATION_JSON_VALUE)
    public CompletableFuture<List<Post>> getAll() {
        return this.posts.readAllBy();
    }

    @PostMapping(value = "", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> create(@RequestBody @Valid CreatePostCommand dto) {
        var data = Post.builder().title(dto.title()).content(dto.content()).build();
        var saved = this.posts.save(data);
        //publishing an event.
        this.eventPublisher.publishPostCreated(new PostCreated(saved.getId(), saved.getTitle(), saved.getCreatedAt()));
        return created(URI.create("/posts/" + saved.getId())).build();
    }
}

