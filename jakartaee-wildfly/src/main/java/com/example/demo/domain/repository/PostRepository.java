package com.example.demo.domain.repository;

import com.example.demo.domain.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID>,
        QuerydslPredicateExecutor<Post>,
        JpaSpecificationExecutor<Post>,
        PostRepositoryCustom {
}
