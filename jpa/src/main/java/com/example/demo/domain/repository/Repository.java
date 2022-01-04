package com.example.demo.domain.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface Repository<E, ID> {

    EntityManager entityManager();

    private Class<E> entityClazz() {
        return (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    default List<E> findAll() {
        CriteriaBuilder cb = this.entityManager().getCriteriaBuilder();
        // create query
        CriteriaQuery<E> query = cb.createQuery(this.entityClazz());
        // set the root class
        Root<E> root = query.from(this.entityClazz());
        // perform query
        return this.entityManager().createQuery(query).getResultList();
    }

    default Stream<E> stream() {
        CriteriaBuilder cb = this.entityManager().getCriteriaBuilder();
        // create query
        CriteriaQuery<E> query = cb.createQuery(this.entityClazz());
        // set the root class
        Root<E> root = query.from(this.entityClazz());
        // perform query and return result as Stream for further operations
        return this.entityManager().createQuery(query).getResultStream();
    }

    default E findById(ID id) {
        E entity = null;
        try {
            entity = this.entityManager().find(this.entityClazz(), id);
        } catch (NoResultException e) {
            //e.printStackTrace();
            System.err.println("NoResultException: " + e.getMessage());
        }
        return entity;
    }

    default Optional<E> findOptionalById(ID id) {
        E entity = null;
        try {
            entity = this.entityManager().find(this.entityClazz(), id);
        } catch (NoResultException e) {
            //e.printStackTrace();
            System.err.println("NoResultException: " + e.getMessage());
        }
        return Optional.ofNullable(entity);
    }

    @Transactional
    default E save(E entity) {
        if (this.entityManager().contains(entity)) {
            return this.entityManager().merge(entity);
        } else {
            this.entityManager().persist(entity);
            return entity;
        }
    }

    @Transactional
    default void delete(E entity) {
        this.entityManager().refresh(entity);
        this.entityManager().remove(entity);
    }
}