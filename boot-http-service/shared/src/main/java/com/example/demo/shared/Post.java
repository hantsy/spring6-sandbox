package com.example.demo.shared;

import java.time.LocalDateTime;
import java.util.UUID;

public record Post(UUID id,
                   String title,
                   String content,
                   Status status,
                   LocalDateTime createdAt
) {
}
