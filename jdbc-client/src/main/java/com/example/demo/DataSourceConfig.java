package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Slf4j
@Configuration
@PropertySource(value = "classpath:/datasource.properties", ignoreResourceNotFound = true)
public class DataSourceConfig {

    private static final String ENV_DATASOURCE_PASSWORD = "datasource.password";
    private static final String ENV_DATASOURCE_USERNAME = "datasource.username";
    private static final String ENV_DATASOURCE_URL = "datasource.url";

    @Autowired
    private Environment env;


    @Bean
    @Profile("default")
    public DataSource defaultDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(env.getProperty(ENV_DATASOURCE_URL));
        dataSource.setUsername(env.getProperty(ENV_DATASOURCE_USERNAME));
        dataSource.setPassword(env.getProperty(ENV_DATASOURCE_PASSWORD));
        log.debug("exposing a Datasource bean...");
        return dataSource;
    }


}