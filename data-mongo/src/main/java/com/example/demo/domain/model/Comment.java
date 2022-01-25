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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Document
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post implements Serializable {

    @Id
    UUID id;

    //@Field(name = "title", targetType = FieldType.STRING, write = Field.Write.ALWAYS)
    private String title;
    private String content;
    @Builder.Default
    private Set<String> labels = new HashSet<>();
    @Builder.Default
    private Status status = Status.DRAFT;
    @CreatedDate
    private LocalDateTime createdAt;
    @CreatedBy
    private String createdBy;
}