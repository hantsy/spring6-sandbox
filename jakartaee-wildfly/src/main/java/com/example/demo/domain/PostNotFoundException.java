package com.example.demo.domain;

import java.util.UUID;

public class PostNotFoundException extends RuntimeException{
    public PostNotFoundException(UUID id) {
        super(String.format("Post %s was not found", id));
    }
}
