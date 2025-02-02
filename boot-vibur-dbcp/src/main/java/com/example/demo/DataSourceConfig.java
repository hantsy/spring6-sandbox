package com.example.demo;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.vibur.dbcp.ViburDBCPDataSource;

import javax.sql.DataSource;


@Configuration(proxyBeanMethods = false)
public class DataSourceConfig {
    public static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    @Bean(initMethod = "start", destroyMethod = "close")
    @Profile({"default", "test"})
    public DataSource viburDataSource(DataSourceProperties dataSourceProperties) {
        log.debug("default dataSourceProperties: url={}, username={}, password={}, driverClassName={}",
                dataSourceProperties.getUrl(),
                dataSourceProperties.getUsername(),
                dataSourceProperties.getPassword(),
                dataSourceProperties.getDriverClassName());
        return DataSourceBuilder.create()
                .type(ViburDBCPDataSource.class)
                .url(dataSourceProperties.getUrl())
                .username(dataSourceProperties.getUsername())
                .password(dataSourceProperties.getPassword())
                .driverClassName(dataSourceProperties.getDriverClassName())
                .build();
    }
}
