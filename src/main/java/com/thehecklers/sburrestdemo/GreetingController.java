package com.thehecklers.sburrestdemo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/greeting")
class GreetingController {

    private final Greeting greeting;

    GreetingController(Greeting greeting) {
        this.greeting = greeting;
    }

    @GetMapping
    String getGreeting() {
        return greeting.name();
    }

    @GetMapping("/coffee")
    String getGreetingWithCoffee() {
        return greeting.coffee();
    }
}
