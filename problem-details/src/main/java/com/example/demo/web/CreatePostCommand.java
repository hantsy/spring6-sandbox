package com.example.demo.web;

import jakarta.validation.constraints.NotBlank;
import org.springframework.util.StringUtils;

// bean validation is not supported on a record class
// see: https://github.com/spring-projects/spring-framework/issues/27868
public record CreatePostCommand(
        @NotBlank String title,
        @NotBlank String content) {
    public CreatePostCommand {
        if (!StringUtils.hasText(title)) {
            throw new IllegalArgumentException("Title is required.");
        }

        if (!StringUtils.hasText(content)) {
            throw new IllegalArgumentException("Content is required");
        }
    }
}
