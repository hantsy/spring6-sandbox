package com.example.demo.jakarta;


import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.web.context.annotation.ApplicationScope;

@ApplicationScope
public class CdiConfig {

    @Produces
    @Dependent
    @PersistenceContext(unitName = "blogPU")
    private EntityManager entityManager;
}
