package com.example.demo.domain.repository;

import com.example.demo.domain.model.Post;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Set;

public class PostRepositoryImpl implements PostRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Post> findPostByLabels(String... labels) {
        var sql = """
            select distinct p 
            from Post p join fetch p.labels label 
            where label in (:l)
            """;
        return this.entityManager.createQuery(sql, Post.class)
            .setParameter("l", Set.of(labels))
            .getResultList();
    }
}
