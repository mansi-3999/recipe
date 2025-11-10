package recipeApp.service;

import org.springframework.lang.NonNull;
import recipeApp.exception.BadRequestException;
import recipeApp.exception.ResourceNotFoundException;
import recipeApp.model.Recipe;
import recipeApp.repository.RecipeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import recipeApp.dto.RecipeSummary;

@Service
public class RecipeService {

    private static final Logger log = LoggerFactory.getLogger(RecipeService.class);

    private final RecipeRepository repository;
    private final SearchService searchService;

    public RecipeService(RecipeRepository repository, SearchService searchService) {
        this.repository = repository;
        this.searchService = searchService;
    }


    public List<Recipe> search(String q) {
        if (q == null || q.trim().isEmpty()) {
            throw new BadRequestException("Search query cannot be empty");
        }
        try {
            return searchService.search(q.trim());
        } catch (Exception e) {
            log.error("Error during search operation", e);
            throw new BadRequestException("Error processing search request: " + e.getMessage());
        }
    }

    public List<RecipeSummary> searchAndSummarize(String q) {
        if (q == null || q.trim().length() < 3) {
            throw new BadRequestException("Query parameter 'q' is required and must be at least 3 characters.");
        }
        try {
            List<Recipe> results = search(q.trim());
            List<RecipeSummary> summaries = results.stream()
                .map(RecipeSummary::from)
                .toList();
            return summaries;
        } catch (Exception e) {
            log.error("Error during search operation", e);
            throw e;
        }
    }

    public Recipe findById(@NonNull Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + id));
    }
    
}
