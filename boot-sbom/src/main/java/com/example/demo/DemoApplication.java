package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}

@RestController
@RequestMapping("/customers")
class CustomerController {

    @GetMapping
    Flux<Customer> getAll() {
        return Flux.just(
                new Customer("Hantsy", "Bar"),
                new Customer("Foo", "Bar")
        );
    }
}

record Customer(String firstName, String lastName) {
}
