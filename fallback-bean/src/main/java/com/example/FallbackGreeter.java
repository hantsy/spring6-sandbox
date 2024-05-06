package com.example;

public class FallbackGreeter implements Greeter {
    @Override
    public String greet() {
        return "Hello fallback";
    }
}
