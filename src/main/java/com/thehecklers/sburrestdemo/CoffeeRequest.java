package com.thehecklers.sburrestdemo;

public record CoffeeRequest(String name) {

    public CoffeeRequest {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
    }
}
