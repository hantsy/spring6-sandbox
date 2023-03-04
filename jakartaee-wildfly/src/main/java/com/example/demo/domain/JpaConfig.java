package com.example.demo.domain;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.UserTransaction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.SharedEntityManagerCreator;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.naming.NamingException;


@Configuration
@EnableTransactionManagement
public class JpaConfig {

    @Bean
    public EntityManagerFactory entityManagerFactory() throws NamingException {
        JndiObjectFactoryBean emf = new JndiObjectFactoryBean();
        emf.setJndiName("java:jboss/jpa/BlogPU");
        emf.afterPropertiesSet();
        return (EntityManagerFactory) emf.getObject();
    }

    @Bean
    public EntityManager entityManager(EntityManagerFactory entityManagerFactory) {
        return SharedEntityManagerCreator.createSharedEntityManager(entityManagerFactory);
    }

    @Resource
    UserTransaction userTransaction;

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new JtaTransactionManager(userTransaction);
    }

}
