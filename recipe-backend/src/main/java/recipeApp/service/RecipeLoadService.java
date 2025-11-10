
package recipeApp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.*;

import recipeApp.model.Recipe;
import recipeApp.repository.RecipeRepository;

@Service
@CacheConfig(cacheNames = "recipesCache")
public class RecipeLoadService {
    private static final Logger log = LoggerFactory.getLogger(RecipeLoadService.class);

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RecipeRepository repository;

    @Autowired
    private SearchService searchService;

    @Autowired
    private WebClient webClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${external.api.url}")
    private String externalUrl;

    @PostConstruct
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 2))
    @CachePut(key = "'all'")
    public int loadFromExternal() {
        try {
            log.info("Fetching recipes from {}", externalUrl);
            String payload = webClient.get().uri(externalUrl).retrieve().bodyToMono(String.class).blockOptional().orElse(null);
            if (payload == null) {
                throw new IllegalStateException("Empty response from external datasource");
            }

            List<Recipe> toSave = new ArrayList<>();
            try {
                toSave = objectMapper.readValue(payload, objectMapper.getTypeFactory().constructCollectionType(List.class, Recipe.class));
            } catch (Exception e) {
                Map<String, Object> root = objectMapper.readValue(payload, Map.class);
                Object items = null;
                if (root.containsKey("recipes")) {
                    items = root.get("recipes");
                } else {
                    for (Object value : root.values()) {
                        if (value instanceof List) {
                            items = value;
                            break;
                        }
                    }
                }
                if (items == null || !(items instanceof List)) {
                    throw new IllegalStateException("Unexpected payload from external datasource");
                }
                String itemsJson = objectMapper.writeValueAsString(items);
                toSave = objectMapper.readValue(itemsJson, objectMapper.getTypeFactory().constructCollectionType(List.class, Recipe.class));
            }

            repository.saveAll(toSave);
            searchService.reindex();
            log.info("Loaded {} recipes into H2", toSave.size());
            cacheManager.getCache("recipesCache").put("all", toSave);
            return toSave.size();
        } catch (Exception e) {
            log.error("Error loading recipes from external datasource", e);
            List<Recipe> cached = cacheManager.getCache("recipesCache").get("all", List.class);
            if (cached != null && !cached.isEmpty()) {
                log.warn("Returning cached recipes due to external API failure");
                repository.saveAll(cached);
                searchService.reindex();
                return cached.size();
            }
            throw new RuntimeException("EXTERNAL_API_UNAVAILABLE: Failed to load recipes from external datasource and no cache available: " + e.getMessage(), e);
        }
    }
}
