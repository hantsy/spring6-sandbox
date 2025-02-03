package com.example.demo;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
import org.vibur.dbcp.ViburDBCPDataSource;

import javax.sql.DataSource;


@Configuration(proxyBeanMethods = false)
@Profile({"default", "test"})
public class DataSourceConfig {
    public static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    //@Bean(initMethod = "start", destroyMethod = "close")
    @Bean
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

    @Bean
    public BeanPostProcessor viburDbcpDataSourceBeanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(
                    @NonNull Object bean, @NonNull String beanName) throws BeansException {
                if (bean instanceof ViburDBCPDataSource ds) {
                    ds.start();
                    return ds;
                }
                return bean;
            }
        };
    }
}
