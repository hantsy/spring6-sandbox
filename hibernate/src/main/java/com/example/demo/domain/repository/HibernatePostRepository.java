package com.example.demo.domain.repository;

import com.example.demo.domain.model.Post;
import com.example.demo.domain.model.Post_;
import com.example.demo.domain.model.Status;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
@Transactional
public class HibernatePostRepository implements PostRepository {

    @Inject
    SessionFactory sessionFactory;

    @Override
    public List<Post> findAll() {
        CriteriaBuilder cb = this.sessionFactory.getCriteriaBuilder();
        // create query
        CriteriaQuery<Post> query = cb.createQuery(Post.class);
        // set the root class
        Root<Post> root = query.from(Post.class);
        //perform query
        return this.sessionFactory.getCurrentSession().createQuery(query).getResultList();
    }

    @Override
    public Stream<Post> stream() {
        CriteriaBuilder cb = this.sessionFactory.getCriteriaBuilder();
        // create query
        CriteriaQuery<Post> query = cb.createQuery(Post.class);
        // set the root class
        Root<Post> root = query.from(Post.class);
        //perform query
        return this.sessionFactory.getCurrentSession().createQuery(query).getResultStream();
    }

    @Override
    public List<Post> findByKeyword(String q, Status status, int offset, int limit) {

        CriteriaBuilder cb = this.sessionFactory.getCriteriaBuilder();
        // create query
        CriteriaQuery<Post> query = cb.createQuery(Post.class);
        // set the root class
        Root<Post> root = query.from(Post.class);

        // compute the query where predicates
        List<Predicate> predicates = new ArrayList<>();
        // if keyword is provided
        if (StringUtils.hasText(q)) {
            predicates.add(
                    cb.or(
                            cb.like(root.get(Post_.title), "%" + q + "%"),
                            cb.like(root.get(Post_.content), "%" + q + "%")
                    )
            );
        }
        // if status is set
        if (status != null) {
            predicates.add(cb.equal(root.get(Post_.status), status));
        }
        query.where(cb.and(predicates.toArray(new Predicate[0])));

        //perform query
        return this.sessionFactory.getCurrentSession().createQuery(query)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public Optional<Post> findById(UUID id) {
        Post Post = null;
        try {
            Post = this.sessionFactory.getCurrentSession().find(Post.class, id);
        } catch (NoResultException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(Post);
    }

    @Override
    public Post save(Post Post) {
        if (Post.getId() == null) {
            this.sessionFactory.getCurrentSession().persist(Post);
            return Post;
        } else {
            return this.sessionFactory.getCurrentSession().merge(Post);
        }
    }

    @Override
    public int updateStatus(UUID id, Status status) {
        CriteriaBuilder cb = this.sessionFactory.getCriteriaBuilder();
        // create update
        CriteriaUpdate<Post> update = cb.createCriteriaUpdate(Post.class);
        // set the root class
        Root<Post> root = update.from(Post.class);
        // set where clause
        update.where(cb.equal(root.get(Post_.id), id));
        update.set(root.get(Post_.status), status);
        // perform update
        return this.sessionFactory.getCurrentSession().createQuery(update).executeUpdate();
    }

    @Override
    public int deleteById(UUID id) {
        CriteriaBuilder cb = this.sessionFactory.getCriteriaBuilder();
        // create delete
        CriteriaDelete<Post> delete = cb.createCriteriaDelete(Post.class);
        // set the root class
        Root<Post> root = delete.from(Post.class);
        // set where clause
        delete.where(cb.equal(root.get(Post_.id), id));
        // perform update
        return this.sessionFactory.getCurrentSession().createQuery(delete).executeUpdate();
    }

    @Override
    public int deleteAll() {
        CriteriaBuilder cb = this.sessionFactory.getCriteriaBuilder();
        // create delete
        CriteriaDelete<Post> delete = cb.createCriteriaDelete(Post.class);
        // set the root class
        Root<Post> root = delete.from(Post.class);
        // perform update
        return this.sessionFactory.getCurrentSession().createQuery(delete).executeUpdate();
    }
}
