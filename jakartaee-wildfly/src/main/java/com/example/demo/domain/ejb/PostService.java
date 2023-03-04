package com.example.demo.domain.ejb;

import com.example.demo.domain.model.Post;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.UUID;

@Stateless
public class PostService {

    @PersistenceContext(name = "blogUP")
    EntityManager entityManager;

    public Post findById(UUID id) {
        return entityManager.find(Post.class, id);
    }
}
