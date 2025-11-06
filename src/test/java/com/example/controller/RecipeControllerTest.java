package com.example.controller;

import com.example.model.Recipe;
import com.example.service.RecipeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecipeController.class)
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecipeService recipeService;

    @Test
    void testSearchEndpoint() throws Exception {
        Recipe recipe = new Recipe(1L, "Test Recipe", "Test Cuisine", "Description", "image.jpg");
        when(recipeService.search(anyString())).thenReturn(List.of(recipe));

        mockMvc.perform(get("/api/recipes/search")
                .param("q", "test")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Recipe"))
                .andExpect(jsonPath("$[0].cuisine").value("Test Cuisine"));
    }

    @Test
    void testGetByIdEndpoint() throws Exception {
        Recipe recipe = new Recipe(1L, "Test Recipe", "Test Cuisine", "Description", "image.jpg");
        when(recipeService.findById(1L)).thenReturn(recipe);

        mockMvc.perform(get("/api/recipes/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Recipe"))
                .andExpect(jsonPath("$.description").value("Description"));
    }

    @Test
    void testLoadEndpoint() throws Exception {
        when(recipeService.loadFromExternal()).thenReturn(5);

        mockMvc.perform(post("/api/recipes/load"))
                .andExpect(status().isOk())
                .andExpect(content().string("Loaded: 5"));
    }
}