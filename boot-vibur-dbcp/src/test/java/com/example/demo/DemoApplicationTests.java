package com.example.demo;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class DemoApplicationTests {
    public static final Logger log = LoggerFactory.getLogger(DemoApplicationTests.class);

    @Autowired
    ProductRepository productRepository;

    @Autowired
    DataSourceProperties dataSourceProperties;

    @Test
    void testDataSource() {
        log.debug("Test dataSourceProperties: url={}, username={}, password={}, driverClassName={}",
                dataSourceProperties.getUrl(),
                dataSourceProperties.getUsername(),
                dataSourceProperties.getPassword(),
                dataSourceProperties.getDriverClassName());
        assertThat(dataSourceProperties.getUrl()).isNotNull();
        assertThat(dataSourceProperties.getUsername()).isNotNull();
        assertThat(dataSourceProperties.getPassword()).isNotNull();
    }

    @Test
    void contextLoads() {
        List<Product> products = productRepository.findAll();
        products.forEach(System.out::println);
        assertThat(products).isNotEmpty();
    }

}
