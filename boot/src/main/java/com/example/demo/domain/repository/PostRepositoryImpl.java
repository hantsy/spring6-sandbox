package com.example.demo.domain.repository;

import com.example.demo.domain.model.Post;
import com.example.demo.domain.model.Post_;
import com.example.demo.domain.model.Status;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PostRepositoryImpl implements PostRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Post> findPostByLabels(String... labels) {
        return this.entityManager.createQuery("select distinct p from Post p join fetch p.labels label where label in (:l)", Post.class)
            .setParameter("l", Set.of(labels))
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

    @Override
    @Transactional
    public int updateStatus(UUID id, Status status) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        // create delete
        CriteriaUpdate<Post> update = cb.createCriteriaUpdate(Post.class);
        // set the root class
        Root<Post> root = update.from(Post.class);

        // setup update clause
        update.set(root.get(Post_.status), status);
        update.where(cb.equal(root.get(Post_.id), id));

        // perform update
        return this.entityManager.createQuery(update).executeUpdate();
    }
}
