package com.example.demo;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PostRepository {
    List<Post> findByTitleContains(String name);

    List<Post> findAll();

    Map<Status, Long> countByStatus();

    Post findById(UUID id);

    UUID save(Post p);

    //see: https://github.com/spring-projects/spring-framework/issues/30949
    //int[] saveAll(List<Post> data);

    Integer update(Post p);

    Integer deleteById(UUID id);

    Integer deleteAll();

    Long count();
}
