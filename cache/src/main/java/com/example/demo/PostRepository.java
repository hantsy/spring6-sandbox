package com.example.demo;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface PostRepository extends CrudRepository<Post, UUID> {
    @Override
    @Cacheable(value = "posts", key = "#p0")
    Optional<Post> findById(UUID id);

    @Override
    @CacheEvict(value = "posts", key = "#result.id")
    Post save(Post post);

    @Override
    @CacheEvict(value = "posts", allEntries = true)
    void deleteAll();
}
