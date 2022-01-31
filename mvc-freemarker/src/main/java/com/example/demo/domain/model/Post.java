package com.example.demo.domain.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("posts")
@Setter
@Getter
@ToString
public class Post {

    @Id
    UUID id;
    String title;
    String content;
    Status status = Status.DRAFT;

    @CreatedDate
    LocalDateTime createdAt;

    public static Post of(String title, String content) {
        var data = new Post();
        data.setTitle(title);
        data.setContent(content);

        return data;
    }

}
