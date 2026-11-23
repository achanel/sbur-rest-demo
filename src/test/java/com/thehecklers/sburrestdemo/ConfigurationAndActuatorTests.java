package com.thehecklers.sburrestdemo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ConfigurationAndActuatorTests {

    @Autowired
    private Greeting greeting;

    @Autowired
    private Droid droid;

    private MockMvc mvc;

    @BeforeEach
    void setUp(WebApplicationContext context) {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void greetingIsBoundFromProperties() {
        assertThat(greeting.name()).isEqualTo("Dakota");
        assertThat(greeting.coffee())
                .isEqualTo("Dakota is drinking Cafe Cereza");
    }

    @Test
    void droidIsBoundFromProperties() {
        assertThat(droid.getId()).isEqualTo("BB-8");
        assertThat(droid.getDescription())
                .isEqualTo("Small, rolling android. Probably doesn't drink coffee.");
    }

    @Test
    void getGreetingReturnsBoundName() throws Exception {
        mvc.perform(get("/greeting"))
                .andExpect(status().isOk())
                .andExpect(content().string("Dakota"));
    }

    @Test
    void getGreetingWithCoffeeResolvesPlaceholder() throws Exception {
        mvc.perform(get("/greeting/coffee"))
                .andExpect(status().isOk())
                .andExpect(content().string("Dakota is drinking Cafe Cereza"));
    }

    @Test
    void getDroidReturnsBoundDroid() throws Exception {
        mvc.perform(get("/droid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("BB-8"))
                .andExpect(jsonPath("$.description")
                        .value("Small, rolling android. Probably doesn't drink coffee."));
    }

    @Test
    void actuatorHealthIsExposedWithDetails() throws Exception {
        mvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.components").exists());
    }

    @Test
    void actuatorConfigPropsIsExposed() throws Exception {
        mvc.perform(get("/actuator/configprops"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("greeting")));
    }
}
