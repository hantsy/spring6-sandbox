package com.example.demo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class DataInitializer implements ApplicationRunner {
    private final ProductRepository productRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        productRepository.saveAll(
                List.of(
                    new Product(null, "Apple", new BigDecimal("5.00")),
                    new Product(null, "Apple", new BigDecimal("5.00"))
                )
            )
            .subscribe(
                data -> log.info("saved data:{}", data),
                error -> log.error("error: " + error),
                () -> log.info("done.")
            );
    }
}
