package com.example.demo.domain.repository;

import com.example.demo.domain.model.Post;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.UUID;

public interface PostRepository extends ListCrudRepository<Post, UUID>, PostRepositoryCustom {
    List<Post> findByTitleContains(String name);

}
