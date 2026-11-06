package com.thehecklers.sburrestdemo;

import java.util.UUID;

public record Coffee(String id, String name) {

    public Coffee {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
    }

    public Coffee(String name) {
        this(UUID.randomUUID().toString(), name);
    }
}
