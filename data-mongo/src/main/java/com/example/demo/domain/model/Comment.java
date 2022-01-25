package com.example.demo.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

@Document("comments")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment implements Serializable {

    @Id
    String id;
    private String content;
    @CreatedDate
    private LocalDateTime createdAt;
    @CreatedBy
    private String createdBy;

    public static Comment of(String content) {
        return Comment.builder().content(content).build();
    }
}