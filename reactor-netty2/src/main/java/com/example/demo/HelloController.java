package com.example.demo;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/greeting")
public class HelloController {

    @GetMapping
    public Mono<String> sayHello(@RequestParam("name") String name){
        return Mono.just("Hello, "+ name);
    }

}
