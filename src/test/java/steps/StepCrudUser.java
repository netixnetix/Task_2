package steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.User;
import java.util.HashMap;
import java.util.Map;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;


public final class StepCrudUser {

    private static final String CREATE_USER_PATH = "/api/auth/register";
    private static final String GET_UPDATE_DELETE_USER_PATH = "/api/auth/user";


    private StepCrudUser() {
    }

    @Step("Создать пользователя")
    public static Response create(User user) {
        Map<String, String> body = new HashMap<>();
        if (user.getEmail() != null) {
            body.put("email", user.getEmail());
        }
        if (user.getName() != null) {
            body.put("name", user.getName());
        }
        if (user.getPassword() != null) {
            body.put("password", user.getPassword());
        }
        return given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(CREATE_USER_PATH);
    }

    @Step("Проверить, что при неполных данных возвращается 403 и сообщение об обязательности полей")
    public static void checkInsufficientData(Response response) {
        response.then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Step("Проверить, что логин уже используется (403)")
    public static void checkLoginAlreadyUsed(Response response) {
        response.then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(403)
                .body("message", equalTo("User already exists"))
                .body("success", equalTo(false));
    }

    @Step("Проверить успешное создание пользователя")
    public static void checkCreated(Response response, User user) {
        response.then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()));
    }

    @Step("Авторизоваться и изменить данные пользователя")
    public static Response updateAuthorizedUser (Response authorizeResponse, User newUserData) {
         return given()
                .log().ifValidationFails()
                .header("Authorization", authorizeResponse.path("accessToken") )
                .contentType(ContentType.JSON)
                .body(newUserData)
                .when()
                .patch("/api/auth/user");
    }

    @Step("Выполнить запрос на изменение данных пользователя без авторизации")
    public static Response updateUserDataUnauthorized (User newUserData) {
        return given()
                .log().ifValidationFails()
                .contentType(ContentType.JSON)
                .body(newUserData)
                .when()
                .patch("/api/auth/user");
    }

    @Step("Проверить в теле измененные данные пользователя")
    public static void checkUpdateUser(Response updateUserResponse, User newUserData) {
        updateUserResponse.then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(newUserData.getEmail()))
                .body("user.name", equalTo(newUserData.getName()));

    }

    @Step("Проверить в теле невозможность изменения пользовательских данных")
    public static void checkNotUpdateUser(Response updateUserResponse) {
        updateUserResponse.then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));

    }

    @Step("Проверить в теле невозможность изменения e-mail пользователя")
    public static void checkNotUpdateUserEmail(Response updateUserResponse) {
        updateUserResponse.then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User with such email already exists"));

    }

    @Step("Удалить пользователя (под авторизацией)")
    public static Response deleteUser(Response authResponse) {
        return given()
                .log().ifValidationFails()
                .header("Authorization", authResponse.path("accessToken"))
                .when()
                .delete(GET_UPDATE_DELETE_USER_PATH);
    }

}
