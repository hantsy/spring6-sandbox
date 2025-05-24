package com.example.demo.shared;

import java.util.UUID;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(UUID id) {
        super("Post: " + id + " not found");
    }
}
