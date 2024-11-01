package diplome.ingridientsDto;

import java.util.List;

public class IngredientsRequest {
    private List<String> ingredients;

    // Геттер и сеттер
    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}
