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

import java.util.List;

@RestController
@RequestMapping("/coffees")
class RestApiDemoController {

    private final CoffeeRepository coffees;

    RestApiDemoController(CoffeeRepository coffees) {
        this.coffees = coffees;
    }

    @GetMapping
    List<Coffee> getCoffees() {
        return coffees.findAll();
    }

    @GetMapping("/{id}")
    ResponseEntity<Coffee> getCoffeeById(@PathVariable String id) {
        return coffees.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Coffee postCoffee(@RequestBody CoffeeRequest request) {
        return coffees.save(new Coffee(request.name()));
    }

    @PutMapping("/{id}")
    ResponseEntity<Coffee> putCoffee(@PathVariable String id, @RequestBody CoffeeRequest request) {
        return coffees.findById(id)
                .map(existing -> {
                    existing.rename(request.name());
                    return ResponseEntity.ok(coffees.save(existing));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(coffees.save(new Coffee(id, request.name()))));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCoffee(@PathVariable String id) {
        coffees.findById(id).ifPresent(coffees::delete);
    }
}
