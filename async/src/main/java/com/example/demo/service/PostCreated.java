package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.UUID;

public record PostCreated(UUID postId, String title, LocalDateTime createdAt) {
}
