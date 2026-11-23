package com.thehecklers.sburrestdemo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.stream.Stream;

@SpringBootApplication
public class SburrestdemoApplication {

    static void main(String[] args) {
        SpringApplication.run(SburrestdemoApplication.class, args);
    }

    @Bean
    CommandLineRunner seedCoffees(CoffeeRepository coffees) {
        return args -> {
            if (coffees.count() == 0) {
                coffees.saveAll(Stream.of(
                                "Café Cereza",
                                "Café Ganador",
                                "Café Lareño",
                                "Café Três Pontas")
                        .map(Coffee::new)
                        .toList());
            }
        };
    }
}
