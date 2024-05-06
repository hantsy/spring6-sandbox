package com.example;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class Printer {
    private final Greeter greeter;

    @Getter
    private String message;

    public void print() {
        this.message = this.greeter.greet();
        log.debug("print the greeting message: {}", this.message);
    }
}
