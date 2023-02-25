package com.example.demo.domain.model;

import jakarta.persistence.Embeddable;
import org.springframework.util.StringUtils;

import java.util.Objects;

//@Embeddable
public record Address(String street, String city, String postalCode) {
    public Address {
        if (!StringUtils.hasText(postalCode)) {
            throw new IllegalArgumentException("The Address postalCode can not be null");
        }
    }
}
