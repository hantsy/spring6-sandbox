package com.example.demo.web;

import com.example.demo.domain.model.Post;
import com.example.demo.domain.model.Status;
import com.example.demo.domain.repository.PostRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
@Validated
public class PostController {
    private final PostRepository posts;

    @GetMapping(value = "", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Post>> getAll(@RequestParam(defaultValue = "") String q,
                                             @RequestParam(defaultValue = "") String status,
                                             @RequestParam(defaultValue = "0") int offset,
                                             @RequestParam(defaultValue = "10") int limit) {
        var postStatus = StringUtils.hasText(status) ? Status.valueOf(status) : null;
        var data = this.posts.findByKeyword(q, postStatus, offset, limit);
        return ok(data);
    }

    @PostMapping(value = "", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> create(@RequestBody @Valid CreatePostCommand dto) {
        var data = Post.builder().title(dto.title()).content(dto.content()).build();
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
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody @Valid UpdatePostCommand dto) {
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
        var deleted = posts.deleteById(id);
        if (deleted > 0) {
            return noContent().build();
        } else {
            return notFound().build();
        }
    }
}

