
package recipeApp.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import recipeApp.model.Recipe;
import recipeApp.service.RecipeService;
import recipeApp.service.RecipeLoadService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import recipeApp.dto.RecipeSummary;
import recipeApp.dto.ErrorResponse;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/v1/api/recipes")

public class RecipeController {
    private final RecipeService service;
    private final RecipeLoadService recipeLoadService;

    public RecipeController(RecipeService service, RecipeLoadService recipeLoadService) {
        this.service = service;
        this.recipeLoadService = recipeLoadService;
    }


    @PostMapping("/load")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Recipes loaded successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request - invalid input or parameters"),
        @ApiResponse(responseCode = "500", description = "Server error or external API unavailable")
    })
    public String load() {
        int count = recipeLoadService.loadFromExternal();
        return "Loaded: " + count;
    }

    @GetMapping("/search")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Search successful, recipes returned"),
        @ApiResponse(responseCode = "400", description = "Bad request - invalid query parameter"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    public List<RecipeSummary> search(@RequestParam(name = "q", required = false) String q) {
        try {
            return service.searchAndSummarize(q);
        } catch (Exception e) {
            return List.of();
        }
    }

    @GetMapping("/{id}")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Recipe found and returned"),
        @ApiResponse(responseCode = "400", description = "Bad request - invalid ID"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    public Recipe getById(@PathVariable("id") @NonNull Long id) {
        return service.findById(id);
    }

    @GetMapping
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fetched all recipes successfully"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    public List<RecipeSummary> getAllRecipes() {
        try {
            return service.getAllRecipes()
                    .stream()
                    .map(RecipeSummary::from)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }

}
