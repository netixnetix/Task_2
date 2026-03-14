package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    private List<String> ingredients;

    public static CreateOrderRequest withIngredients(List<String> ingredientIds) {
        return new CreateOrderRequest(ingredientIds);
    }

    public static CreateOrderRequest empty() {
        return new CreateOrderRequest(List.of());
    }

    public static CreateOrderRequest withInvalidIngredientId(String invalidId) {
        return new CreateOrderRequest(List.of(invalidId));
    }

    public static final String INVALID_INGREDIENT_ID = "000000000000000000000000";
}
