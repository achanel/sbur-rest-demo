package com.thehecklers.sburrestdemo;

/**
 * Бин из application.properties: droid.id и droid.description.
 * Создаётся в SburrestdemoApplication через @Bean + @ConfigurationProperties,
 * поэтому нужны JavaBean-сеттеры.
 */
public class Droid {

    private String id;
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
