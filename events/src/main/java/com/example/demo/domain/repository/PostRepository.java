package com.example.demo.domain.repository;

import com.example.demo.domain.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PostRepository extends JpaRepository<Post, UUID> {

    @Async
    CompletableFuture<List<Post>> readAllBy();

    @Query("""
            UPDATE Post p
            SET p.commentsCount=p.commentsCount+1
            WHERE p.id=:id
            """)
    @Modifying
    Long increaseCommentsCount(@Param("id") UUID postId);
}
