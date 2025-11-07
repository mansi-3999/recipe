package recipeApp.repository;

import recipeApp.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @Query("SELECT r FROM Recipe r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(r.cuisine) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Recipe> searchByNameOrCuisine(@Param("q") String q);
}
