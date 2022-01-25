package com.example.demo.domain.repository;

import com.example.demo.domain.model.Post;

import java.util.List;

public interface PostRepositoryCustom {
    List<Post> findPostByLabels(String... labels);
}
