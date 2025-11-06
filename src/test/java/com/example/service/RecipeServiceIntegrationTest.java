package com.example.service;

import com.example.model.Recipe;
import com.example.repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@DataJpaTest
@Import({RecipeService.class, SearchService.class})
@TestPropertySource(properties = {
    "recipes.datasource.url=http://test.example.com/recipes",
    "spring.jpa.properties.hibernate.search.backend.directory.root=target/test-indexes"
})
class RecipeServiceIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RecipeService recipeService;

    @MockBean
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        Recipe recipe1 = new Recipe(null, "Pasta Carbonara", "Italian", "Classic pasta dish", "pasta1.jpg");
        Recipe recipe2 = new Recipe(null, "Pizza Margherita", "Italian", "Classic pizza", "pizza1.jpg");
        Recipe recipe3 = new Recipe(null, "Sushi Roll", "Japanese", "Fresh sushi", "sushi1.jpg");
        
        entityManager.persist(recipe1);
        entityManager.persist(recipe2);
        entityManager.persist(recipe3);
        entityManager.flush();
    }

    @Test
    void testSearch() {
        var results = recipeService.search("pasta");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).contains("Pasta");
    }

    @Test
    void testSearchByCuisine() {
        var results = recipeService.search("italian");
        assertThat(results).hasSize(2);
    }

    @Test
    void testFindById() {
        Recipe found = recipeService.findById(1L);
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Pasta Carbonara");
    }
}