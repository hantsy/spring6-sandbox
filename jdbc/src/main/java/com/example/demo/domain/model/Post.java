package com.example.demo.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record Post(UUID id,
                   String title,
                   String content,
                   Status status,
                   LocalDateTime createdAt
) {
    public Post(String title, String content) {
        this(null, title, content, Status.DRAFT, null);
    }

    public Post(String title, String content, Status status) {
        this(null, title, content, status, null);
    }
}
