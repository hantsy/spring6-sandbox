package com.example.demo.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record CreatePostCommand(@NotBlank @Size(min = 5) String title, @NotBlank String content) {
}
