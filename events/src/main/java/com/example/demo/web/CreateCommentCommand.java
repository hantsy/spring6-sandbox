package com.example.demo.web;

import jakarta.validation.constraints.NotEmpty;

public record CreateCommentCommand(@NotEmpty String content) {
}
