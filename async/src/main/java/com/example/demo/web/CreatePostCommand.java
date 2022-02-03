package com.example.demo.web;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record CreatePostCommand(@NotEmpty @Size(min = 5) String title, @NotEmpty String content) {
}
