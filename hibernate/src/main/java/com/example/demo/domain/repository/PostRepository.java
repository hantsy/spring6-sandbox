package com.example.demo.domain.repository;

import com.example.demo.domain.model.Post;
import com.example.demo.domain.model.Status;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

// To simplify the Repository interface definition, you can create `interface PostRepository extends Repository<Post, UUID>`
public interface PostRepository {
    List<Post> findAll();

    Stream<Post> stream();

    List<Post> findByKeyword(String q, Status status, int offset, int limit);

    Optional<Post> findById(UUID id);

    Post save(Post Post);

    int updateStatus(UUID id, Status status);

    int deleteById(UUID id);

    int deleteAll();
}
