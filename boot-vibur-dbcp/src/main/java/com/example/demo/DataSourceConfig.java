package com.example.demo;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.vibur.dbcp.ViburDBCPDataSource;

import javax.sql.DataSource;


@Configuration(proxyBeanMethods = false)
@Profile({"default", "test"})
public class DataSourceConfig {
    public static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    @Bean(initMethod = "start", destroyMethod = "close")
    public DataSource viburDataSource(DataSourceProperties dataSourceProperties,
                                      ObjectProvider<JdbcConnectionDetails> connectionDetailsObjectProvider) {
        if (dataSourceProperties.getUrl() == null) {
            log.debug("No url provided, it should start testcontainers Postgres service");
            connectionDetailsObjectProvider.ifAvailable(jdbcConnectionDetails -> {
                log.debug("Datasource url is null, fill it with Jdbc connection details: {}", jdbcConnectionDetails);
                dataSourceProperties.setUrl(jdbcConnectionDetails.getJdbcUrl());
                dataSourceProperties.setUsername(jdbcConnectionDetails.getUsername());
                dataSourceProperties.setPassword(jdbcConnectionDetails.getPassword());
                dataSourceProperties.setDriverClassName(jdbcConnectionDetails.getDriverClassName());
            });
        }
        String url = dataSourceProperties.getUrl();
        String username = dataSourceProperties.getUsername();
        String password = dataSourceProperties.getPassword();
        String driverClassName = dataSourceProperties.getDriverClassName();
        log.debug("Final merged DataSourceProperties: url={}, username={}, password={}, driverClassName={}",
                url,
                username,
                password,
                driverClassName);
        return DataSourceBuilder.create()
                .type(ViburDBCPDataSource.class)
                .url(url)
                .username(username)
                .password(password)
                .driverClassName(driverClassName)
                .build();
    }
}
