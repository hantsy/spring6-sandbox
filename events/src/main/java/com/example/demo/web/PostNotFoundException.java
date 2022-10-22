package com.example.demo.web;

import java.util.UUID;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(UUID id) {
        super("post #" + id + " was not found");
    }
}
