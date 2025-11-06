package com.example.service;

import com.example.exception.BadRequestException;
import com.example.exception.ResourceNotFoundException;
import com.example.model.Recipe;
import com.example.repository.RecipeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class RecipeService {

    private final RecipeRepository repository;
    private final SearchService searchService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String externalUrl;
    private final Logger logger = LoggerFactory.getLogger(RecipeService.class);

    public RecipeService(RecipeRepository repository, SearchService searchService, RestTemplate restTemplate, @Value("${recipes.datasource.url}") String externalUrl) {
        this.repository = repository;
        this.searchService = searchService;
        this.restTemplate = restTemplate;
        this.externalUrl = externalUrl;
    }

    public List<Recipe> search(String q) {
        if (q == null || q.trim().isEmpty()) {
            throw new BadRequestException("Search query cannot be empty");
        }
        try {
            return searchService.search(q.trim());
        } catch (Exception e) {
            logger.error("Error during search operation", e);
            throw new BadRequestException("Error processing search request: " + e.getMessage());
        }
    }

    public Recipe findById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + id));
    }

    @Transactional
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 2))
    public int loadFromExternal() throws Exception {
        logger.info("Fetching recipes from {}", externalUrl);
        String payload = restTemplate.getForObject(externalUrl, String.class);
        if (payload == null) {
            throw new IllegalStateException("Empty response from external datasource");
        }

        JsonNode root = objectMapper.readTree(payload);
        JsonNode items = null;
        if (root.has("recipes")) {
            items = root.get("recipes");
        } else if (root.isArray()) {
            items = root;
        } else {
            // try to detect an array field
            Iterator<String> names = root.fieldNames();
            while (names.hasNext()) {
                String name = names.next();
                if (root.get(name).isArray()) {
                    items = root.get(name);
                    break;
                }
            }
        }

        if (items == null || !items.isArray()) {
            throw new IllegalStateException("Unexpected payload from external datasource");
        }

        List<Recipe> toSave = new ArrayList<>();
        for (JsonNode node : items) {
            Long id = node.has("id") ? node.get("id").asLong() : null;
            if (id == null) continue;

            String name = null;
            if (node.has("title")) name = node.get("title").asText();
            else if (node.has("name")) name = node.get("name").asText();
            else if (node.has("recipeName")) name = node.get("recipeName").asText();
            if (name == null) name = "";

            String cuisine = node.has("cuisine") ? node.get("cuisine").asText() : null;
            String description = node.has("description") ? node.get("description").asText() : null;

            String image = null;
            if (node.has("image")) image = node.get("image").asText();
            else if (node.has("images") && node.get("images").isArray() && node.get("images").size() > 0) image = node.get("images").get(0).asText();

            // parse instructions (array or text)
            List<String> instructions = null;
            if (node.has("instructions")) {
                JsonNode instrNode = node.get("instructions");
                if (instrNode.isArray()) {
                    instructions = new ArrayList<>();
                    for (JsonNode s : instrNode) {
                        if (s.isTextual()) instructions.add(s.asText());
                        else instructions.add(s.toString());
                    }
                } else if (instrNode.isTextual()) {
                    // split textual instructions into steps by newlines
                    String txt = instrNode.asText();
                    String[] parts = txt.split("\\r?\\n");
                    instructions = new ArrayList<>();
                    for (String p : parts) if (!p.isBlank()) instructions.add(p.trim());
                }
            }

            // parse ingredients (array)
            List<String> ingredients = null;
            if (node.has("ingredients") && node.get("ingredients").isArray()) {
                ingredients = new ArrayList<>();
                for (JsonNode it : node.get("ingredients")) {
                    ingredients.add(it.asText());
                }
            }

            // fallbacks for description
            if (description == null || description.isBlank()) {
                if (node.has("summary")) description = node.get("summary").asText();
                else if (node.has("about")) description = node.get("about").asText();
                else if (instructions != null && !instructions.isEmpty()) {
                    // use first few steps as a short description
                    int n = Math.min(3, instructions.size());
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < n; i++) {
                        if (i > 0) sb.append("\n\n");
                        sb.append(instructions.get(i));
                    }
                    description = sb.toString();
                }
            }

            // servings and times
            Integer servings = null;
            if (node.has("servings")) servings = node.get("servings").asInt();
            else if (node.has("yield")) servings = node.get("yield").asInt();

            String prepTime = null;
            if (node.has("prepTime")) prepTime = node.get("prepTime").asText();
            else if (node.has("prep_time")) prepTime = node.get("prep_time").asText();
            else if (node.has("prepMinutes")) prepTime = node.get("prepMinutes").asText();

            String cookTime = null;
            if (node.has("cookTime")) cookTime = node.get("cookTime").asText();
            else if (node.has("cook_time")) cookTime = node.get("cook_time").asText();
            else if (node.has("cookMinutes")) cookTime = node.get("cookMinutes").asText();
            else if (node.has("readyInMinutes")) cookTime = node.get("readyInMinutes").asText();

            // Additional fields with numeric values
            Integer prepTimeMinutes = null;
            if (node.has("prepTimeMinutes")) prepTimeMinutes = node.get("prepTimeMinutes").asInt();
            else if (node.has("prep_time_minutes")) prepTimeMinutes = node.get("prep_time_minutes").asInt();

            Integer cookTimeMinutes = null;
            if (node.has("cookTimeMinutes")) cookTimeMinutes = node.get("cookTimeMinutes").asInt();
            else if (node.has("cook_time_minutes")) cookTimeMinutes = node.get("cook_time_minutes").asInt();

            String difficulty = null;
            if (node.has("difficulty")) difficulty = node.get("difficulty").asText();
            else if (node.has("skill_level")) difficulty = node.get("skill_level").asText();

            Integer caloriesPerServing = null;
            if (node.has("caloriesPerServing")) caloriesPerServing = node.get("caloriesPerServing").asInt();
            else if (node.has("calories")) caloriesPerServing = node.get("calories").asInt();
            else if (node.has("calories_per_serving")) caloriesPerServing = node.get("calories_per_serving").asInt();

            // build recipe using setters to avoid constructor mismatch
            Recipe r = new Recipe();
            r.setId(id);
            r.setName(name);
            r.setCuisine(cuisine);
            r.setDescription(description);
            r.setImage(image);
            r.setInstructions(instructions);
            r.setIngredients(ingredients);
            r.setServings(servings);
            r.setPrepTime(prepTime);
            r.setCookTime(cookTime);
            r.setPrepTimeMinutes(prepTimeMinutes);
            r.setCookTimeMinutes(cookTimeMinutes);
            r.setDifficulty(difficulty);
            r.setCaloriesPerServing(caloriesPerServing);

            toSave.add(r);
        }

        repository.saveAll(toSave);
        searchService.reindex(); // rebuild search index
        logger.info("Loaded {} recipes into H2", toSave.size());
        return toSave.size();
    }
}
