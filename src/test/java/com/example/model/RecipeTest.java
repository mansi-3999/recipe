package com.example.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class RecipeTest {

    @Test
    void testRecipeCreation() {
        Recipe recipe = new Recipe(1L, "Pasta", "Italian", "Classic pasta dish", "pasta.jpg");
        
        assertThat(recipe.getId()).isEqualTo(1L);
        assertThat(recipe.getName()).isEqualTo("Pasta");
        assertThat(recipe.getCuisine()).isEqualTo("Italian");
        assertThat(recipe.getDescription()).isEqualTo("Classic pasta dish");
        assertThat(recipe.getImage()).isEqualTo("pasta.jpg");
    }
}