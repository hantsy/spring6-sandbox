package com.example.demo.event.transactional;

import java.time.LocalDateTime;
import java.util.UUID;

public record PostCreatedEvent(UUID postId, String title, LocalDateTime createdAt) {
}
