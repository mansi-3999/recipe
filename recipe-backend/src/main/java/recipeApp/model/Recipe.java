package recipeApp.model;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "recipes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String cuisine;

    @Column(length = 2000)
    private String description;

    private String image;

    // Optional extended fields populated from external datasource
    @ElementCollection
    @CollectionTable(name = "recipe_instructions", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "instruction", length = 2000)
    private List<String> instructions;

    @ElementCollection
    @CollectionTable(name = "recipe_ingredients", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "ingredient")
    private List<String> ingredients;

    private Integer servings;
    private String prepTime;
    private String cookTime;
    private Integer prepTimeMinutes;
    private Integer cookTimeMinutes;
    private String difficulty;
    private Integer caloriesPerServing;

    // Keep a convenience constructor compatible with existing tests and code
    public Recipe(Long id, String name, String cuisine, String description, String image) {
        this.id = id;
        this.name = name;
        this.cuisine = cuisine;
        this.description = description;
        this.image = image;
    }
}
