package apiTest;

import config.ApiConfig;
import data.FakerData;
import io.restassured.response.Response;
import jdk.jfr.Description;
import models.CreateOrderRequest;
import models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import steps.StepCrudUser;
import steps.StepLoginUser;
import steps.StepIngredients;
import steps.StepOrders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;

public class OrdersTest {

    private final List<Response> authResponsesForCleanup = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        ApiConfig.setUp();
    }

    @AfterEach
    public void cleanup() {
        for (Response authResponse : authResponsesForCleanup) {
            if (authResponse != null && authResponse.getStatusCode() == 200) {
                StepCrudUser.deleteUser(authResponse);
            }
        }
        authResponsesForCleanup.clear();
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и с ингредиентами")
    @Description("POST /api/orders с Authorization и валидными ingredients возвращает 200, success true, name и order")
    public void createOrderWithAuthAndIngredients() {
        User user = new User(FakerData.email(), FakerData.name(), FakerData.pwd());
        Response createResponse = StepCrudUser.create(user);
        authResponsesForCleanup.add(StepLoginUser.signIn(user));

        CreateOrderRequest orderRequest = CreateOrderRequest.withIngredients(StepIngredients.getFirstIngredientIds(2));
        Response orderResponse = StepOrders.createOrderAuthorized(createResponse, orderRequest);
        StepOrders.checkCreateOrderSuccess(orderResponse);
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("POST /api/orders без Authorization с валидными ingredients — 200")
    public void createOrderWithoutAuth() {
        User user = new User(FakerData.email(), FakerData.name(), FakerData.pwd());
        StepCrudUser.create(user);
        authResponsesForCleanup.add(StepLoginUser.signIn(user));

        CreateOrderRequest orderRequest = CreateOrderRequest.withIngredients(StepIngredients.getFirstIngredientIds(2));
        Response orderResponse = StepOrders.createOrderUnauthorized(orderRequest);
        StepOrders.checkCreateOrderSuccess(orderResponse);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("POST /api/orders с пустым ingredients возвращает 400 и сообщение Ingredient ids must be provided")
    public void createOrderWithoutIngredients() {
        User user = new User(FakerData.email(), FakerData.name(), FakerData.pwd());
        Response createResponse = StepCrudUser.create(user);
        authResponsesForCleanup.add(StepLoginUser.signIn(user));

        Response orderResponse = StepOrders.createOrderWithEmptyIngredients(createResponse);
        StepOrders.checkCreateOrderWithoutIngredients(orderResponse);
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    @Description("POST /api/orders с невалидным хешем ингредиента возвращает 400")
    public void createOrderWithInvalidIngredientHash() {
        User user = new User(FakerData.email(), FakerData.name(), FakerData.pwd());
        Response createResponse = StepCrudUser.create(user);
        authResponsesForCleanup.add(StepLoginUser.signIn(user));

        Response orderResponse = StepOrders.createOrderWithInvalidIngredientHash(createResponse, CreateOrderRequest.INVALID_INGREDIENT_ID);
        StepOrders.checkCreateOrderInvalidIngredientHash(orderResponse);
    }

    @Test
    @DisplayName("Получение заказов конкретного пользователя — авторизованный пользователь")
    @Description("GET /api/orders с Authorization возвращает 200, success true и список orders")
    public void getOrdersAuthorized() {
        User user = new User(FakerData.email(), FakerData.name(), FakerData.pwd());
        StepCrudUser.create(user);
        Response loginResponse = StepLoginUser.signIn(user);
        authResponsesForCleanup.add(loginResponse);

        Response ordersResponse = StepOrders.getOrdersAuthorized(loginResponse);
        StepOrders.checkGetOrdersSuccess(ordersResponse);
    }

    @Test
    @DisplayName("Получение заказов конкретного пользователя — неавторизованный пользователь")
    @Description("GET /api/orders без Authorization возвращает 401 и сообщение You should be authorised")
    public void getOrdersUnauthorized() {
        Response ordersResponse = StepOrders.getOrdersUnauthorized();
        StepOrders.checkGetOrdersUnauthorized(ordersResponse);
    }
}
