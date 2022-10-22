package com.example.demo.event.transactional;

import java.time.LocalDateTime;
import java.util.UUID;

public record PostCreated(UUID postId, String title, LocalDateTime createdAt) {
}
