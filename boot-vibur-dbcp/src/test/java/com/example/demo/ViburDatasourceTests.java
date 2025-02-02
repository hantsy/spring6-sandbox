package com.example.demo;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
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
@ActiveProfiles("vibur")
@Testcontainers
class ViburDatasourceTests {
    private static final Logger log = LoggerFactory.getLogger(ViburDatasourceTests.class);

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
        registry.add("spring.datasource.vibur.name", () -> "vibur");
        registry.add("spring.datasource.vibur.pool-initial-size", () -> 2);
        registry.add("spring.datasource.vibur.pool-max-size", () -> 10);
        registry.add("spring.datasource.vibur.connection-timeout-in-ms", () -> 5000);
        registry.add("spring.datasource.vibur.login-timeout-in-seconds", () -> 2);
        registry.add("spring.datasource.vibur.log-query-execution-longer-than-ms", () -> 5);
        registry.add("spring.datasource.vibur.log-connection-longer-than-ms", () -> 5);
        registry.add("spring.datasource.vibur.clear-SQL-warnings", () -> false);
    }

    @TestConfiguration
    @EnableConfigurationProperties(ViburProperties.class)
    static class TestConfig {

        @Bean(initMethod = "start", destroyMethod = "close")
        @Profile("vibur")
        DataSource testDataSource(DataSourceProperties dataSourceProperties,
                                  ViburProperties viburProperties) {
            log.debug("vibur properties: {}", viburProperties);
            var dataSource = DataSourceBuilder.create()
                    .type(ViburDBCPDataSource.class)
                    .url(dataSourceProperties.getUrl())
                    .username(dataSourceProperties.getUsername())
                    .password(dataSourceProperties.getPassword())
                    .driverClassName(dataSourceProperties.getDriverClassName())
                    .build();

            dataSource.setPoolInitialSize(viburProperties.poolInitialSize());
            dataSource.setPoolMaxSize(viburProperties.poolMaxSize());
            dataSource.setLoginTimeout(viburProperties.loginTimeoutInSeconds());
            dataSource.setConnectionTimeoutInMs(viburProperties.connectionTimeoutInMs());
            dataSource.setLogConnectionLongerThanMs(viburProperties.logConnectionLongerThanMs());
            dataSource.setLogQueryExecutionLongerThanMs(viburProperties.logQueryExecutionLongerThanMs());
            dataSource.setClearSQLWarnings(viburProperties.clearSQLWarnings());
            dataSource.setName(viburProperties.name());
            return dataSource;
        }
    }

    // add extra Vibur Dbcp properties
    //see: https://github.com/spring-projects/spring-boot/issues/42903
    @ConfigurationProperties(prefix = "spring.datasource.vibur")
    static record ViburProperties(
            String name,
            int poolInitialSize,
            int poolMaxSize,
            int connectionTimeoutInMs,
            int loginTimeoutInSeconds,
            int logQueryExecutionLongerThanMs,
            int logConnectionLongerThanMs,
            boolean clearSQLWarnings
    ) {

    }

    @Autowired
    DataSource dataSource;

    @Test
    void contextLoads() {
        assertThat(dataSource).isInstanceOf(ViburDBCPDataSource.class);
        ViburDBCPDataSource ds = (ViburDBCPDataSource) dataSource;
        assertThat(ds.getName()).isEqualTo("vibur");
        assertThat(ds.getUsername()).isEqualTo("user");
    }

}
