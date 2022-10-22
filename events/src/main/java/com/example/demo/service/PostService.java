package com.example.demo.service;

import com.example.demo.domain.model.Comment;
import com.example.demo.domain.model.PostId;
import com.example.demo.domain.repository.CommentRepository;
import com.example.demo.event.transactional.CommentCreatedEvent;
import com.example.demo.event.transactional.CommentCreatedEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final CommentRepository comments;
    private final CommentCreatedEventPublisher commentCreatedEventPublisher;

    @Transactional
    public Comment addCommentToPost(String content, UUID postId) {
        var comment = comments.save(Comment.builder().content(content).postId(new PostId(postId)).build());
        commentCreatedEventPublisher.publishCommentCreated(new CommentCreatedEvent(comment));
        return comment;
    }


}
