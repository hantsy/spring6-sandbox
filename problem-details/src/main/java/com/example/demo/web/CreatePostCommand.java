package com.example.demo.web;

import jakarta.validation.constraints.NotBlank;

// bean validation is not supported on a record class
// see: https://github.com/spring-projects/spring-framework/issues/27868
public record CreatePostCommand(
        @NotBlank String title,
        @NotBlank String content) {
    public CreatePostCommand {
        if (title == null) {
            throw new IllegalArgumentException("Title is required.");
        }

        if (content == null) {
            throw new IllegalArgumentException("Content is required");
        }
    }
}
