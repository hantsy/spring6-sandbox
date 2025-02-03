package com.example.demo;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@ActiveProfiles("test")
class ProductRepositoryTest {
    public static final Logger log = LoggerFactory.getLogger(ProductRepositoryTest.class);

    @Autowired
    ProductRepository productRepository;

    @Test
    void contextLoads() {
        List<Product> products = productRepository.findAll();
        products.forEach(System.out::println);
        assertThat(products).isNotEmpty();
    }

}
