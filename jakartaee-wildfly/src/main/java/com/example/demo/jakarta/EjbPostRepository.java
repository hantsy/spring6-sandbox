package com.example.demo.jakarta;

import com.example.demo.domain.model.Post;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.UUID;

@Stateless
public class EjbPostRepository {

    @PersistenceContext(name = "blogUP")
    EntityManager entityManager;

    public Post findById(UUID id) {
        return entityManager.find(Post.class, id);
    }

    public List<Post> findAll() {
        return entityManager.createQuery("select p from Post p", Post.class)
            .getResultList();
    }
}
