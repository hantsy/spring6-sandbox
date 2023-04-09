package com.example.demo.domain;


import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.web.context.annotation.ApplicationScope;

@ApplicationScope
public class JpaCdiConfig {

    @Produces
    @Dependent
    @PersistenceContext(unitName = "blogPU")
    private EntityManager entityManager;
}
