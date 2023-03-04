package com.example.demo.domain.repository;

import com.example.demo.domain.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID>, PostRepositoryCustom {
}
