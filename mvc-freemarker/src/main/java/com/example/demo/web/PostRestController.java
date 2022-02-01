package com.example.demo.web;

import com.example.demo.domain.model.Post;
import com.example.demo.domain.model.PostSummary;
import com.example.demo.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.*;


@Controller
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostRestController {
    private final PostRepository posts;

    @GetMapping(value = "search", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<PostSummary>> searchByKeyword(@RequestParam(defaultValue = "") String q,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        var data = this.posts.findByTitleContains(q, PageRequest.of(page, size));
        return ok(data);
    }

    @GetMapping(value = "", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll() {
        var data = this.posts.findAll();
        return ok(data);
    }

    @PostMapping(value = "", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> create(@RequestBody CreatePostCommand dto) {
        var data = Post.of(dto.title(), dto.content());
        var saved = this.posts.save(data);
        return created(URI.create("/posts/" + saved.getId())).build();
    }

    @GetMapping(value = "{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        return posts.findById(id)
                .map(p -> ok(p))
                .orElseGet(() -> notFound().build());
    }

    @PutMapping(value = "{id}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdatePostCommand dto) {
        return posts.findById(id)
                .map(p -> {
                    p.setTitle(dto.title());
                    p.setContent(dto.content());
                    p.setStatus(dto.status());
                    this.posts.save(p);
                    return noContent().build();
                })
                .orElseGet(() -> notFound().build());
    }

    @DeleteMapping(value = "{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {
        try {
            posts.deleteById(id);
            return noContent().build();
        } catch (EmptyResultDataAccessException e) {
            return notFound().build();
        }
    }
}

