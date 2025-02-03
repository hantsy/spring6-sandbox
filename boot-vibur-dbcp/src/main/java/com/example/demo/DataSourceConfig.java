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
            log.debug("No url property provided, try to read connection details from Testcontainers service");
            var jdbcConnectionDetails = connectionDetailsObjectProvider.getIfAvailable();
            if (jdbcConnectionDetails != null) {
                log.debug("Build datasource from connection details: {}", jdbcConnectionDetails);
                return DataSourceBuilder.create()
                        .type(ViburDBCPDataSource.class)
                        .url(jdbcConnectionDetails.getJdbcUrl())
                        .username(jdbcConnectionDetails.getUsername())
                        .password(jdbcConnectionDetails.getPassword())
                        .driverClassName(jdbcConnectionDetails.getDriverClassName())
                        .build();
            }
        }

        String url = dataSourceProperties.getUrl();
        String username = dataSourceProperties.getUsername();
        String password = dataSourceProperties.getPassword();
        String driverClassName = dataSourceProperties.getDriverClassName();
        log.debug("Build DataSource from properties: url={}, username={}, password={}, driverClassName={}",
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
