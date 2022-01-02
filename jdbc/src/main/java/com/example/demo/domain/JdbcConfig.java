package com.example.demo.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.jdbc.datasource.init.CompositeDatabasePopulator;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

@Configuration
public class JdbcConfig {

    @Bean
    NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

//    @Bean
//    TransactionAwareDataSourceProxy transactionAwareDataSourceProxy(DataSource dataSource) {
//        return new TransactionAwareDataSourceProxy(dataSource);
//    }

    @Bean
    PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    TransactionTemplate transactionTemplate(PlatformTransactionManager jdbcTransactionManager) {
        return new TransactionTemplate(jdbcTransactionManager);
    }

    @Bean
    DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
        var initializer = new DataSourceInitializer();

        var databasePopulator = new CompositeDatabasePopulator();
        databasePopulator.addPopulators(
                new ResourceDatabasePopulator(new ClassPathResource("/schema.sql")),
                new ResourceDatabasePopulator(new ClassPathResource("/data.sql"))
        );

        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(databasePopulator);
        return initializer;
    }
}

