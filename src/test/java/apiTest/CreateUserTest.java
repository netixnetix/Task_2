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


public class CreateUserTest {
    private final List<Response> authResponsesForCleanup = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        ApiConfig.setUp();
    }

    @AfterEach
    public void cleanup() {
        UserCleanupHelper.cleanupCreatedUsers(authResponsesForCleanup);
    }

    @Test
    @DisplayName("Успешное создание пользователя")
    @Description("Создание пользователя с валидными login, password, firstName возвращает 201 и ok: true")
    public void createNewCourier() {
        User user = new User(FakerData.email(),FakerData.name(), FakerData.pwd());
        Response response = StepCrudUser.create(user);
        authResponsesForCleanup.add(StepLoginUser.signIn(user));
        StepCrudUser.checkCreated(response, user);

    }


    @Test
    @DisplayName("Нельзя создать второго курьера с тем же email")
    @Description("Повторное создание пользователя с тем же email возвращает 409 и сообщение о ранее созданном User")
    public void notCreateNewCourierIfLoginUse() {
        User user = new User(FakerData.email(),FakerData.name(), FakerData.pwd());

        Response response = StepCrudUser.create(user);
        StepCrudUser.checkCreated(response, user);
        authResponsesForCleanup.add(StepLoginUser.signIn(user));


        Response response2 = StepCrudUser.create(user);
        StepCrudUser.checkLoginAlreadyUsed(response2);

    }

    @ParameterizedTest(name = "Нельзя создать пользователя без указания {0}")
    @Description("При попытке создания пользователя без одного из полей API возвращает 403 и сообщение об обязательности полей")
    @CsvSource({
            "email",
            "name",
            "password"
    })
    public void notCreateNewCourierIfNotFilled(String missingField) {
        String email = "email".equals(missingField) ? null : FakerData.email();
        String name = "name".equals(missingField) ? null : FakerData.name();
        String password = "password".equals(missingField) ? null : FakerData.pwd();
        User user = new User(email, name, password);

        Response response = StepCrudUser.create(user);
        StepCrudUser.checkInsufficientData(response);
    }


}
