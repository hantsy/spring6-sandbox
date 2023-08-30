package com.example.demo.web;

import java.io.Serializable;
import java.time.LocalDateTime;

public record PostSummary(String title, LocalDateTime createdAt) {
}
