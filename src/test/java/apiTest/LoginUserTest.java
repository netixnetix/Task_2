package apiTest;

import config.ApiConfig;
import data.FakerData;
import io.restassured.response.Response;
import jdk.jfr.Description;
import models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import steps.StepCrudUser;
import steps.StepLoginUser;
import util.UserCleanupHelper;

import java.util.ArrayList;
import java.util.List;


public class LoginUserTest {

    private final List<Response> authResponsesForCleanup = new ArrayList<>();

    @BeforeEach
    public void setUp(){
        ApiConfig.setUp();
    }

    @AfterEach
    public void cleanup() {
        UserCleanupHelper.cleanupCreatedUsers(authResponsesForCleanup);
    }

    @Test
    @DisplayName("Успешная авторизация под созданным пользователем")
    @Description("При успешно авторизации POST /api/auth/login , возвращаем 200 код, accessToken / refreshToken / success / user data")
    public void loginSuccess() {
        User user = new User(FakerData.email(),FakerData.name(), FakerData.pwd());
        StepCrudUser.create(user);
        authResponsesForCleanup.add(StepLoginUser.signIn(user));

        Response response2 = StepLoginUser.signIn(user);
        StepLoginUser.checkLoginSuccess(response2, user);
    }

    @ParameterizedTest(name = "Нельзя авторизоваться под пользователем, передав {0}")
    @Description("При некорректной авторизации POST /api/auth/login , возвращает 401 код, success false, сообщение об ошибке")
    @CsvSource({
            "inCorrectEmail",
            "blankEmail",
            "blankPassword",
            "passwordToUpperCase",
            "passwordToLowerCase",
            "correctPassword+invalidChar"
    })
    public void loginNoSuccess(String missingField) {
        String email = FakerData.email();
        String name = FakerData.name();
        String password = FakerData.pwd();

        User user = new User(email, name,password);
        StepCrudUser.create(user);
        authResponsesForCleanup.add(StepLoginUser.signIn(user));


        email = "inCorrectEmail".equals(missingField) ? "1"+email : email;
        email = "blankEmail".equals(missingField) ? " " : email;
        password = "blankPassword".equals(missingField) ? " " : password;
        password = "passwordToUpperCase".equals(missingField) ? password.toUpperCase() : password;
        password = "passwordToLowerCase".equals(missingField) ? password.toLowerCase() : password;
        password = "correctPassword+invalidChar".equals(missingField) ? password + "!" : password;


        User userInCorrect = new User(email,name, password);

        Response response = StepLoginUser.signIn(userInCorrect);
        StepLoginUser.checkInCorrectSingIn(response);
    }
}
