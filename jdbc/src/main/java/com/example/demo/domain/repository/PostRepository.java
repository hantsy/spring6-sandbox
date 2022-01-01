package com.example.demo.domain.repository;

import com.example.demo.domain.model.CreatePostCommand;
import com.example.demo.domain.model.Post;
import com.example.demo.domain.model.UpdatePostCommand;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PostRepository {
    List<Post> findByTitleContains(String name);

    List<Post> findAll();

    List<Map<String, Object>> countByStatus();

    Post findById(UUID id);

    UUID save(CreatePostCommand p);

    int[] saveAll(List<Post> data);

    Integer update(UUID id, UpdatePostCommand p);

    Integer deleteById(UUID id);

    Integer deleteAll();
}
