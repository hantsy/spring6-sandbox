package com.example.demo;

import java.math.BigDecimal;

public record Product(Long id, String name, BigDecimal price) {
}
