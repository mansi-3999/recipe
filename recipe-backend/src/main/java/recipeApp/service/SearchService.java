package recipeApp.service;

import recipeApp.model.Recipe;
import recipeApp.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchService.class);

    private final RecipeRepository recipeRepository;

    @Transactional(readOnly = true)
    public List<Recipe> search(String text) {
        return recipeRepository.searchByNameOrCuisine(text);
    }

    @Transactional
    public void reindex() {
        log.info("No reindexing needed with standard JPA search");
    }
}
