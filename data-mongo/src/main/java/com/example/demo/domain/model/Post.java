package com.example.demo.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;


@Document("posts")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post implements Serializable {

    // A Document `id` can be a `String`, `BigInteger` or mongo type ObjectId
    @Id
    String id;

    @Field(name = "title", targetType = FieldType.STRING, write = Field.Write.ALWAYS)
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

    @Field("slug")
    private String slug;

    @DocumentReference
    List<Comment> comments = new ArrayList<>();
}