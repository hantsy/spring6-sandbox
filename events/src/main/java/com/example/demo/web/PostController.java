package com.example.demo.web;

import com.example.demo.domain.model.Post;
import com.example.demo.domain.repository.PostRepository;
import com.example.demo.event.transactional.PostCreatedEvent;
import com.example.demo.event.transactional.PostCreatedEventPublisher;
import com.example.demo.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.created;

@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
@Validated//does not work on record.
public class PostController {
    private final PostRepository posts;
    private final PostCreatedEventPublisher eventPublisher;

    private final PostService postService;

    @GetMapping(value = "", produces = APPLICATION_JSON_VALUE)
    public CompletableFuture<List<Post>> getAll() {
        return this.posts.readAllBy();
    }

    @PostMapping(value = "", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> create(@RequestBody @Valid CreatePostCommand dto) {
        var data = Post.builder().title(dto.title()).content(dto.content()).build();
        var saved = this.posts.save(data);
        //publishing an event.
        this.eventPublisher.publishPostCreated(new PostCreatedEvent(saved.getId(), saved.getTitle(), saved.getCreatedAt()));
        return created(URI.create("/posts/" + saved.getId())).build();
    }

    @GetMapping(value = "{id}")
    public ResponseEntity<Post> getById(@PathVariable UUID id) {
        return posts.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new PostNotFoundException(id));
    }

    @PostMapping(value = "{id}/comments", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createComment(@PathVariable UUID id, @RequestBody @Valid CreateCommentCommand dto) {
        var post = posts.findById(id).orElseThrow(() -> new PostNotFoundException(id));
        var savedComment = postService.addCommentToPost(dto.content(), post.getId());
        return created(URI.create("/comments/" + savedComment.getId())).build();
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<Object> handle(Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", e.getMessage()));
    }
}

