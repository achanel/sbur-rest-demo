package com.thehecklers.sburrestdemo;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Приветствие из application.properties: greeting.name и greeting.coffee.
 * Находится через @ConfigurationPropertiesScan на приложении (constructor binding).
 */
@ConfigurationProperties(prefix = "greeting")
public record Greeting(String name, String coffee) {
}
