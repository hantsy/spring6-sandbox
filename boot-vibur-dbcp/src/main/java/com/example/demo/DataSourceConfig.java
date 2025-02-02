package com.example.demo;


import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vibur.dbcp.ViburDBCPDataSource;

import javax.sql.DataSource;

//see: https://github.com/spring-projects/spring-boot/issues/42903
@Configuration(proxyBeanMethods = false)
public class DataSourceConfig {

    @Bean(initMethod = "start", destroyMethod = "close")
    public DataSource viburDataSource() {
        return DataSourceBuilder.create()
                .type(ViburDBCPDataSource.class)
                .build();
    }
}
