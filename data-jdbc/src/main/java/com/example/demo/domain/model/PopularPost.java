package com.example.demo.domain.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("popular_posts")
public record PopularPost(
        @Id
        UUID id,
        String title,
        String content,
        @CreatedDate @Column("created_at")
        LocalDateTime createdAt,
        @Version
        Long version
) {
//        public PopularPost(String title, String content) {
//                this(null, title, content, null);
//        }
//
}

