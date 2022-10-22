package com.example.demo.event.transactional;

import com.example.demo.domain.model.Comment;

public record CommentCreatedEvent(Comment eventData) {
}
