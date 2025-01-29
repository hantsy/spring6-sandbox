package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.vibur.dbcp.ViburDBCPDataSource;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("ds")
@Testcontainers
class ViburDatasourceTests {

    @Container
    static PostgreSQLContainer PG_CONTAINER = new PostgreSQLContainer("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("user")
            .withPassword("password");

    @DynamicPropertySource
    static void setupDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", PG_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.password", PG_CONTAINER::getPassword);
        registry.add("spring.datasource.username", PG_CONTAINER::getUsername);
    }

    @TestConfiguration
    static class TestConfig {

        @Bean(initMethod = "start", destroyMethod = "close")
        DataSource viburDataSource(DataSourceProperties dataSourceProperties) {
            return DataSourceBuilder.create()
                    .type(ViburDBCPDataSource.class)
                    .url(dataSourceProperties.getUrl())
                    .username(dataSourceProperties.getUsername())
                    .password(dataSourceProperties.getPassword())
                    .driverClassName(dataSourceProperties.getDriverClassName())
                    .build();
        }
    }

    @Autowired
    DataSource dataSource;

    @Test
    void contextLoads() {
        assertThat(dataSource).isInstanceOf(ViburDBCPDataSource.class);
        ViburDBCPDataSource ds = (ViburDBCPDataSource) dataSource;
        assertThat(ds.getUsername()).isEqualTo("user");
    }

}
