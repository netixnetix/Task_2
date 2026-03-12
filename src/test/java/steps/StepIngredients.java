package steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.mapper.ObjectMapperType;
import models.Ingredient;
import models.IngredientsResponse;

import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public final class StepIngredients {

    private static final String INGREDIENTS_PATH = "/api/ingredients";

    private StepIngredients() {
    }

    @Step("Получить список ингредиентов")
    public static Response getIngredients() {
        return given()
                .log().ifValidationFails()
                .contentType(ContentType.JSON)
                .when()
                .get(INGREDIENTS_PATH);
    }

    @Step("Извлечь id первых N ингредиентов для заказа")
    public static List<String> getFirstIngredientIds(int count) {
        IngredientsResponse ingredientsResponse = getIngredientsResponse();
        return ingredientsResponse.getData().stream()
                .limit(count)
                .map(Ingredient::get_id)
                .collect(Collectors.toList());
    }

    public static IngredientsResponse getIngredientsResponse() {
        Response response = getIngredients();
        return response.as(IngredientsResponse.class, ObjectMapperType.GSON);
    }


}
