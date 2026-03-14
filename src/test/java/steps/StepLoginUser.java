package steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.User;
import org.junit.jupiter.api.Assertions;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class StepLoginUser {

    private static final String LOGIN_USER_PATH = "/api/auth/login";


    @Step("Авторизоваться под пользователем")
    public static Response signIn(User user) {
        return given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "email", user.getEmail(),
                        "password", user.getPassword()
                ))
                .when()
                .post(LOGIN_USER_PATH);
    }

    @Step("Проверить успешный вход пользователя, 200 код")
    public static void checkLoginSuccess(Response response, User user) {
        response.then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", allOf(notNullValue(), startsWith("Bearer ")))
                .body("refreshToken", notNullValue())
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()));
    }

    @Step("Проверить некорректность данных для входа")
    public static void checkInCorrectSingIn(Response response) {
        response.then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(401)
                .and().body("message", equalTo("email or password are incorrect"))
                .body("success", equalTo(false))
        ;
    }

}