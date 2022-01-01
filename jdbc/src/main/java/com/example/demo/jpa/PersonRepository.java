package com.example.demo.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional("jpaTransactionManager")
public class PersonRepository {

    @PersistenceContext
    EntityManager entityManager;

    public List<Person> findAll() {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        // create query
        CriteriaQuery<Person> query = cb.createQuery(Person.class);
        // set the root class
        Root<Person> root = query.from(Person.class);
        //perform query
        return this.entityManager.createQuery(query).getResultList();
    }


    public List<Person> findByKeyword(String q, int offset, int limit) {

        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        // create query
        CriteriaQuery<Person> query = cb.createQuery(Person.class);
        // set the root class
        Root<Person> root = query.from(Person.class);

        // if keyword is provided
        if (q != null && !q.trim().isEmpty()) {
            query.where(
                    cb.or(
                            cb.like(root.get(Person_.firstName), "%" + q + "%"),
                            cb.like(root.get(Person_.lastName), "%" + q + "%")
                    )
            );
        }
        //perform query
        return this.entityManager.createQuery(query)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public Optional<Person> findById(UUID id) {
        Person Person = null;
        try {
            Person = this.entityManager.find(Person.class, id);
        } catch (NoResultException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(Person);
    }

    public Person save(Person Person) {
        if (Person.getId() == null) {
            this.entityManager.persist(Person);
            return Person;
        } else {
            return this.entityManager.merge(Person);
        }
    }

    public int deleteById(UUID id) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        // create delete
        CriteriaDelete<Person> delete = cb.createCriteriaDelete(Person.class);
        // set the root class
        Root<Person> root = delete.from(Person.class);
        // set where clause
        delete.where(cb.equal(root.get(Person_.id), id));
        // perform update
        return this.entityManager.createQuery(delete).executeUpdate();
    }
}
