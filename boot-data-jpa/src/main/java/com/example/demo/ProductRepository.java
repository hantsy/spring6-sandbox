package com.example.demo;

import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Window;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Window<Product> findFirst10ByNameContains(String name, ScrollPosition position);
}
