package com.example.demo.domain.repository;

import com.example.demo.domain.model.Post;
import com.example.demo.domain.model.Post_;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.Root;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PostRepositoryImpl implements PostRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Post> findPostByLabels(String... labels) {
        return this.entityManager.createQuery("select distinct p from Post p join fetch p.labels label where label in (:l)", Post.class)
                .setParameter("l", Arrays.stream(labels).collect(Collectors.toSet()))
                .getResultList();
    }

    @Override
    @Transactional
    public int customDeleteById(UUID id) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        // create delete
        CriteriaDelete<Post> delete = cb.createCriteriaDelete(Post.class);
        // set the root class
        Root<Post> root = delete.from(Post.class);
        // set where clause
        delete.where(cb.equal(root.get(Post_.id), id));
        // perform update
        return this.entityManager.createQuery(delete).executeUpdate();
    }

    @Override
    @Transactional
    public int customDeleteAll() {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        // create delete
        CriteriaDelete<Post> delete = cb.createCriteriaDelete(Post.class);
        // set the root class
        Root<Post> root = delete.from(Post.class);
        // perform update
        return this.entityManager.createQuery(delete).executeUpdate();
    }
}
