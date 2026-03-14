package steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.CreateOrderRequest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public final class StepOrders {

    private static final String ORDERS_PATH = "/api/orders";

    private StepOrders() {
    }

    @Step("Создать заказ с авторизацией")
    public static Response createOrderAuthorized(Response authResponse, CreateOrderRequest request) {
        return given()
                .log().ifValidationFails()
                .header("Authorization", authResponse.path("accessToken"))
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(ORDERS_PATH);
    }

    @Step("Создать заказ без авторизации")
    public static Response createOrderUnauthorized(CreateOrderRequest request) {
        return given()
                .log().ifValidationFails()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(ORDERS_PATH);
    }

    @Step("Создать заказ без ингредиентов (с авторизацией)")
    public static Response createOrderWithEmptyIngredients(Response authResponse) {
        return createOrderAuthorized(authResponse, CreateOrderRequest.empty());
    }

    @Step("Создать заказ с невалидным хешем ингредиентов (с авторизацией)")
    public static Response createOrderWithInvalidIngredientHash(Response authResponse, String invalidIngredientId) {
        return createOrderAuthorized(authResponse, CreateOrderRequest.withInvalidIngredientId(invalidIngredientId));
    }

    @Step("Проверить успешное создание заказа")
    public static void checkCreateOrderSuccess(Response response) {
        response.then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order", notNullValue());
    }

    @Step("Проверить, что заказ без ингредиентов возвращает 400")
    public static void checkCreateOrderWithoutIngredients(Response response) {
        response.then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Step("Проверить, что невалидный хеш ингредиентов возвращает 400")
    public static void checkCreateOrderInvalidIngredientHash(Response response) {
        response.then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("One or more ids provided are incorrect"));
    }

    @Step("Получить заказы пользователя с авторизацией")
    public static Response getOrdersAuthorized(Response authResponse) {
        return given()
                .log().ifValidationFails()
                .header("Authorization", authResponse.path("accessToken"))
                .contentType(ContentType.JSON)
                .when()
                .get(ORDERS_PATH);
    }

    @Step("Получить заказы пользователя без авторизации")
    public static Response getOrdersUnauthorized() {
        return given()
                .log().ifValidationFails()
                .contentType(ContentType.JSON)
                .when()
                .get(ORDERS_PATH);
    }

    @Step("Проверить успешное получение заказов пользователя")
    public static void checkGetOrdersSuccess(Response response) {
        response.then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders", notNullValue());
    }

    @Step("Проверить, что без авторизации получение заказов возвращает 401")
    public static void checkGetOrdersUnauthorized(Response response) {
        response.then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}
