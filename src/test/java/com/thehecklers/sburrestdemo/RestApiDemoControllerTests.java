package com.thehecklers.sburrestdemo;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RestApiDemoControllerTests {

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new RestApiDemoController()).build();
    }

    @Test
    void getCoffeesReturnsSeededCoffeesInInsertionOrder() throws Exception {
        mvc.perform(get("/coffees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].name").value("Café Cereza"))
                .andExpect(jsonPath("$[3].name").value("Café Três Pontas"))
                .andExpect(jsonPath("$[*].id").isNotEmpty());
    }

    @Test
    void getCoffeeByIdReturnsExistingCoffee() throws Exception {
        mvc.perform(get("/coffees/{id}", firstCoffeeId()))
                .andExpect(status().isOk())
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
        String id = firstCoffeeId();

        mvc.perform(put("/coffees/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"ignored\",\"name\":\"Café Renamed\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Café Renamed"));

        mvc.perform(get("/coffees/{id}", id))
                .andExpect(jsonPath("$.name").value("Café Renamed"));
        mvc.perform(get("/coffees"))
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].id").value(id));
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
        String id = firstCoffeeId();

        mvc.perform(put("/coffees/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/coffees/{id}", id))
                .andExpect(jsonPath("$.name").value("Café Cereza"));
    }

    @Test
    void deleteCoffeeRemovesCoffeeAndIsIdempotent() throws Exception {
        String id = firstCoffeeId();

        mvc.perform(delete("/coffees/{id}", id))
                .andExpect(status().isNoContent());
        mvc.perform(get("/coffees/{id}", id))
                .andExpect(status().isNotFound());
        mvc.perform(get("/coffees"))
                .andExpect(jsonPath("$.length()").value(3));

        mvc.perform(delete("/coffees/{id}", id))
                .andExpect(status().isNoContent());
    }

    private String firstCoffeeId() throws Exception {
        String body = mvc.perform(get("/coffees"))
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$[0].id");
    }
}
