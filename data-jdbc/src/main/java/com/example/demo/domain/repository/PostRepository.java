package com.example.demo.domain.repository;

import com.example.demo.domain.model.Post;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface PostRepository extends CrudRepository<Post, UUID>, PostRepositoryCustom {
    List<Post> findByTitleContains(String name);

}
