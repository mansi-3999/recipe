package recipeApp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data  // ✅ Automatically generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor  // ✅ Generates a no-argument constructor (required by JPA)
@AllArgsConstructor  // ✅ Generates a full-argument constructor
@Entity
@Table(name = "recipes")
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

    public Recipe(Long id, String name, String cuisine, String description, String image) {
        this.id = id;
        this.name = name;
        this.cuisine = cuisine;
        this.description = description;
        this.image = image;
    }
}
