package com.example.demo.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "posts")
@Getter
@Setter
@SuperBuilder()
@NoArgsConstructor
@AllArgsConstructor
public class Post extends AuditableEntity {
    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "comments_count", columnDefinition = "BIGINT NOT NULL DEFAULT 0")
    private Long commentsCount = 0L;

    @Column(name = "status")
    @Builder.Default
    private Status status = Status.DRAFT;

}