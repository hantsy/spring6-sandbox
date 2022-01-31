package com.example.demo.web;

import com.example.demo.domain.model.Status;

public record UpdatePostCommand(String title, String content, Status status) {
}
