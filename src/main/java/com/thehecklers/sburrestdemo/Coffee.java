package com.thehecklers.sburrestdemo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class Coffee {

    @Id
    private String id;
    private String name;

    protected Coffee() {
        // for JPA
    }

    Coffee(String name) {
        this(UUID.randomUUID().toString(), name);
    }

    Coffee(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    void rename(String name) {
        this.name = name;
    }
}
