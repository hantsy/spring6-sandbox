package com.example.demo.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record PostSummary(UUID id, String title, LocalDateTime createdAt) {
}
