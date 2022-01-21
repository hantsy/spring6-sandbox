package com.example.demo.web;

import com.example.demo.domain.model.Status;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UpdatePostCommand(@NotEmpty String title, @NotEmpty String content, @NotNull Status status) {
}
