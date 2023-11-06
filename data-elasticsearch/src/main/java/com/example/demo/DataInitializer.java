package com.example.demo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Profile("!test")
@Slf4j
@RequiredArgsConstructor
class DataInitializer {

    private final ProductRepository productRepository;

    @EventListener(value = ContextRefreshedEvent.class)
    public void init() {
        log.debug("start data initialization...");
        productRepository.saveAll(
                List.of(
                        new Product(null, "Apple", BigDecimal.TEN),
                        new Product(null, "Orange", BigDecimal.ONE)
                )
        );
        productRepository.findAll().forEach(product -> log.debug("saved product: {}", product));
        log.debug("done data initialization...");
    }
}