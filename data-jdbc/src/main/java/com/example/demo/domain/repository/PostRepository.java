package com.example.demo.domain.repository;

import com.example.demo.domain.model.Post;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PostRepository extends CrudRepository<Post, UUID> {
    List<Post> findByTitleContains(String name);

    List<Map<String, Object>> countByStatus();
}
