package com.example.demo.web;

import com.example.demo.domain.model.Post;
import com.example.demo.domain.model.Status;
import com.example.demo.domain.repository.PostRepository;
import com.example.demo.domain.repository.Predicates;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
@Validated
public class PostController {
    private final PostRepository posts;

    @GetMapping(value = "", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<PaginatedResult<PostSummary>> getAll(@RequestParam(defaultValue = "") String q,
                                                    @RequestParam(defaultValue = "") String status,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        var postStatus = StringUtils.hasText(status) ? Status.valueOf(status) : null;
        var data = this.posts.findAll(Predicates.findByKeyword(q, postStatus), PageRequest.of(page, size))
                .map(p -> new PostSummary(p.getTitle(), p.getCreatedAt()));
        return ok(new PaginatedResult<PostSummary>(data.getContent(), data.getTotalElements()));
    }

    @PostMapping(value = "", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> create(@RequestBody @Valid CreatePostCommand dto) {
        var data = Post.builder().title(dto.title()).content(dto.content()).build();
        var saved = this.posts.save(data);
        return created(URI.create("/posts/" + saved.getId())).build();
    }

    @GetMapping(value = "{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getById(@PathVariable String id) {
        return posts.findById(id)
                .map(p -> ok(p))
                .orElseGet(() -> notFound().build());
    }

    @PutMapping(value = "{id}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody @Valid UpdatePostCommand dto) {
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
    public ResponseEntity<?> deleteById(@PathVariable String id) {
        posts.deleteById(id);
        return noContent().build();
    }
}

