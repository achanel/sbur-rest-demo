package com.thehecklers.sburrestdemo;

import org.springframework.data.repository.ListCrudRepository;

interface CoffeeRepository extends ListCrudRepository<Coffee, String> {
}
