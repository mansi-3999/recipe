package recipeApp.controller;

// ...existing code...
import recipeApp.model.Recipe;
import recipeApp.service.RecipeService;
// ...existing code...
// ...existing code...
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/recipes")
public class RecipeController {

    private final RecipeService service;
    // ...existing code...

    public RecipeController(RecipeService service) {
        this.service = service;
    }

    @PostMapping("/load")
    public ResponseEntity<?> load() {
        int count = service.loadFromExternal();
        return ResponseEntity.ok("Loaded: " + count);
    }

    @GetMapping("/search")
    public ResponseEntity<List<RecipeSummary>> search(@RequestParam(name = "q", required = false) String q) {
        if (!StringUtils.hasText(q) || q.trim().length() < 1) {
            return ResponseEntity.ok(List.of());
        }
        List<Recipe> results = service.search(q.trim());
        List<RecipeSummary> summaries = results.stream()
            .map(RecipeSummary::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(summaries);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getById(@PathVariable("id") @NonNull Long id) {
        Recipe recipe = service.findById(id);
        return ResponseEntity.ok(recipe);
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
