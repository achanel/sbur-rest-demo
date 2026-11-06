package com.thehecklers.sburrestdemo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@RestController
@RequestMapping("/coffees")
class RestApiDemoController {

    private final Map<String, Coffee> coffees = Collections.synchronizedMap(new LinkedHashMap<>());

    RestApiDemoController() {
        Stream.of("Café Cereza", "Café Ganador", "Café Lareño", "Café Três Pontas")
                .map(Coffee::new)
                .forEach(coffee -> coffees.put(coffee.id(), coffee));
    }

    @GetMapping
    List<Coffee> getCoffees() {
        return new ArrayList<>(coffees.values());
    }

    @GetMapping("/{id}")
    ResponseEntity<Coffee> getCoffeeById(@PathVariable String id) {
        Coffee coffee = coffees.get(id);
        return coffee == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(coffee);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Coffee postCoffee(@RequestBody Coffee coffee) {
        Coffee created = new Coffee(coffee.name());
        coffees.put(created.id(), created);
        return created;
    }

    @PutMapping("/{id}")
    ResponseEntity<Coffee> putCoffee(@PathVariable String id, @RequestBody Coffee coffee) {
        Coffee updated = new Coffee(id, coffee.name());
        return coffees.put(id, updated) == null
                ? ResponseEntity.status(HttpStatus.CREATED).body(updated)
                : ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCoffee(@PathVariable String id) {
        coffees.remove(id);
    }
}
