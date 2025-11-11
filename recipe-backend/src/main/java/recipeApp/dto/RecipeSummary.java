package recipeApp.dto;

import recipeApp.model.Recipe;

public class RecipeSummary {
    public Long id;
    public String name;
    public String cuisine;
    private String description;
    private String image;

    public static RecipeSummary from(Recipe r) {
        RecipeSummary s = new RecipeSummary();
        s.id = r.getId();
        s.name = r.getName();
        s.cuisine = r.getCuisine();
        s.description = r.getDescription();
        s.image = r.getImage();
        return s;
    }
}
