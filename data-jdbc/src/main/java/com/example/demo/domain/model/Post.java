package com.example.demo.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("posts")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Post {

    @Id
    UUID id;
    String title;
    String content;
    Status status;

    @CreatedDate
    @Column("created_at")
    LocalDateTime createdAt;

    @CreatedBy
    @Column("created_by")
    String createdBy;
}
