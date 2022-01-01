package com.example.demo.domain.model;

public record UpdatePostCommand(String title, String content, Status status) {
}
