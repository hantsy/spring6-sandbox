package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class DemoApplicationTests {

    @Autowired
    ProductRepository productRepository;

    @Test
    void contextLoads() {
        List<Product> products = productRepository.findAll();
        products.forEach(System.out::println);

        assertThat(products).isNotEmpty();
    }

}
