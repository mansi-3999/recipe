package com.example.controller;

import com.example.exception.ResourceNotFoundException;
import com.example.model.Recipe;
import com.example.service.RecipeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService service;
    private final Logger logger = LoggerFactory.getLogger(RecipeController.class);

    public RecipeController(RecipeService service) {
        this.service = service;
    }

    @PostMapping("/load")
    public ResponseEntity<?> load() {
        try {
            int count = service.loadFromExternal();
            return ResponseEntity.ok("Loaded: " + count);
        } catch (Exception e) {
            logger.error("Error loading recipes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<RecipeSummary>> search(@RequestParam(name = "q", required = false) String q) {
        try {
            if (!StringUtils.hasText(q) || q.trim().length() < 1) {
                return ResponseEntity.ok(List.of());
            }
            List<Recipe> results = service.search(q.trim());
            List<RecipeSummary> summaries = results.stream()
                .map(RecipeSummary::from)
                .collect(Collectors.toList());
            return ResponseEntity.ok(summaries);
        } catch (Exception e) {
            logger.error("Error in search operation", e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getById(@PathVariable("id") Long id) {
        try {
            Recipe recipe = service.findById(id);
            return ResponseEntity.ok(recipe);
        } catch (ResourceNotFoundException e) {
            logger.error("Recipe not found", e);
            throw e;
        }
    }

    public static class RecipeSummary {
        public Long id;
        public String name;
        public String cuisine;

        public static RecipeSummary from(Recipe r) {
            RecipeSummary s = new RecipeSummary();
            s.id = r.getId();
            s.name = r.getName();
            s.cuisine = r.getCuisine();
            return s;
        }
    }
}
