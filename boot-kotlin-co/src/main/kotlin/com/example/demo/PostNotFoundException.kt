package com.example.demo

class PostNotFoundException(postId: Long) : RuntimeException("Post:$postId is not found...")