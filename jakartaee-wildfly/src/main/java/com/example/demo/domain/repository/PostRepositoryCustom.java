package com.example.demo.domain.repository;

import com.example.demo.domain.model.Post;
import com.example.demo.domain.model.Status;

import java.util.List;
import java.util.UUID;

public interface PostRepositoryCustom {
    List<Post> findPostByLabels(String... labels);
}
