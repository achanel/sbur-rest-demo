package com.thehecklers.sburrestdemo;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.stream.StreamSupport;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class RestApiDemoControllerTests {

    @Autowired
    private CoffeeRepository coffees;

    private MockMvc mvc;

    @BeforeEach
    void setUp(WebApplicationContext context) {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void getCoffeesReturnsSeededCoffees() throws Exception {
        mvc.perform(get("/coffees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[*].name").value(containsInAnyOrder(
                        "Café Cereza", "Café Ganador", "Café Lareño", "Café Três Pontas")))
                .andExpect(jsonPath("$[*].id").isNotEmpty());
    }

    @Test
    void getCoffeeByIdReturnsExistingCoffee() throws Exception {
        String id = coffeeId("Café Cereza");

        mvc.perform(get("/coffees/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Café Cereza"));
    }

    @Test
    void getCoffeeByIdReturns404ForUnknownId() throws Exception {
        mvc.perform(get("/coffees/{id}", "no-such-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void postCoffeeCreatesCoffeeWithGeneratedIdAndIgnoresClientId() throws Exception {
        String body = mvc.perform(post("/coffees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"client-id\",\"name\":\"Kaffa\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Kaffa"))
                .andReturn().getResponse().getContentAsString();
        String id = JsonPath.read(body, "$.id");

        mvc.perform(get("/coffees/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Kaffa"));
        mvc.perform(get("/coffees/{id}", "client-id"))
                .andExpect(status().isNotFound());
        mvc.perform(get("/coffees"))
                .andExpect(jsonPath("$.length()").value(5));
    }

    @Test
    void postCoffeeRejectsBlankName() throws Exception {
        mvc.perform(post("/coffees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"  \"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/coffees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/coffees"))
                .andExpect(jsonPath("$.length()").value(4));
    }

    @Test
    void putCoffeeUpdatesExistingCoffee() throws Exception {
        String id = coffeeId("Café Cereza");

        mvc.perform(put("/coffees/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"ignored\",\"name\":\"Café Renamed\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Café Renamed"));

        mvc.perform(get("/coffees/{id}", id))
                .andExpect(jsonPath("$.name").value("Café Renamed"));
        mvc.perform(get("/coffees"))
                .andExpect(jsonPath("$.length()").value(4));
    }

    @Test
    void putCoffeeCreatesCoffeeForUnknownId() throws Exception {
        mvc.perform(put("/coffees/{id}", "new-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Kaffa\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("new-id"))
                .andExpect(jsonPath("$.name").value("Kaffa"));

        mvc.perform(get("/coffees/{id}", "new-id"))
                .andExpect(status().isOk());
    }

    @Test
    void putCoffeeRejectsBlankNameAndKeepsExistingCoffee() throws Exception {
        String id = coffeeId("Café Cereza");

        mvc.perform(put("/coffees/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/coffees/{id}", id))
                .andExpect(jsonPath("$.name").value("Café Cereza"));
    }

    @Test
    void deleteCoffeeRemovesCoffeeAndIsIdempotent() throws Exception {
        String id = coffeeId("Café Cereza");

        mvc.perform(delete("/coffees/{id}", id))
                .andExpect(status().isNoContent());
        mvc.perform(get("/coffees/{id}", id))
                .andExpect(status().isNotFound());
        mvc.perform(get("/coffees"))
                .andExpect(jsonPath("$.length()").value(3));

        mvc.perform(delete("/coffees/{id}", id))
                .andExpect(status().isNoContent());
    }

    private String coffeeId(String name) {
        return StreamSupport.stream(coffees.findAll().spliterator(), false)
                .filter(coffee -> coffee.getName().equals(name))
                .findFirst()
                .orElseThrow()
                .getId();
    }
}
