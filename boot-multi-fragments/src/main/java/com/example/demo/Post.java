package com.example.demo;

import java.time.LocalDateTime;
import java.util.List;

public record Post(
        Long id,
        String title,
        Author author,
        String content,
        LocalDateTime createdAt,
        List<Comment> comments
) {
    public Post withId(Long _id) {
        return new Post(_id, this.title, this.author, this.content, this.createdAt, this.comments);
    }
}

record Author(String name) {
    public static Author of(String name) {
        return new Author(name);
    }
}

record Comment(String content, LocalDateTime createdAt) {
}
