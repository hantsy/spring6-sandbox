package com.example.demo.jdbc;

public record UpdatePostCommand(String title, String content, Status status) {
}
