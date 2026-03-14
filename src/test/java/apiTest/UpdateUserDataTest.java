
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


public class UpdateUserDataTest {

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
    @DisplayName("Авторизованный пользователь может изменить email")
    @Description("При успешно авторизации PATCH /api/auth/user , возвращает 200 код, возвращаются обновленные данные по пользователю")
    public void loginSuccessUpdateUserEmail() {
        String email = FakerData.email();
        String name = FakerData.name();
        String password = FakerData.pwd();

        User user = new User(email, name, password);
        Response response = StepCrudUser.create(user);
        User updateUser = new User(123 + email, name, password);
        Response updateUserData = StepCrudUser.updateAuthorizedUser(response, updateUser);
        StepCrudUser.checkUpdateUser(updateUserData, updateUser);
        authResponsesForCleanup.add(StepLoginUser.signIn(updateUser));
    }

    @Test
    @DisplayName("Авторизованный пользователь может изменить password")
    @Description("Если пользователь меняет пароль, PATCH /api/auth/user , " +
            "возвращает 200 код на изменение пароля, пользовать может зайти под новым паролем")
    public void loginSuccessUpdateUserPassword() {
        String email = FakerData.email();
        String name = FakerData.name();
        String password = FakerData.pwd();

        User user = new User(email, name, password);
        Response response = StepCrudUser.create(user);
        User updateUser = new User(email, name, password + "1xzz@!KLJNOIZ*Y)(_U");
        Response updateUserData = StepCrudUser.updateAuthorizedUser(response, updateUser);
        StepCrudUser.checkUpdateUser(updateUserData, updateUser);
        Response signInResponse = StepLoginUser.signIn(updateUser);
        StepLoginUser.checkLoginSuccess(signInResponse, updateUser);
        authResponsesForCleanup.add(signInResponse);
    }

    @Test
    @DisplayName("Авторизованный пользователь может изменить name")
    @Description("При успешно авторизации PATCH /api/auth/user , возвращает 200 код, возвращаются обновленные данные по пользователю")
    public void SuccessUpdateUserName() {
        String email = FakerData.email();
        String name = FakerData.name();
        String password = FakerData.pwd();

        User user = new User(email, name, password);
        Response response = StepCrudUser.create(user);
        User updateUser = new User(email, name + " Алибаевич", password);
        Response updateUserData = StepCrudUser.updateAuthorizedUser(response, updateUser);
        StepCrudUser.checkUpdateUser(updateUserData, updateUser);
        authResponsesForCleanup.add(response);
    }

    @Test
    @DisplayName("Не авторизованный пользователь не может изменять пользовательские данные")
    @Description("Если пользователь не авторизован, PATCH /api/auth/user , возвращает 401 код, и ошибку в теле ответа")
    public void NotSuccessUpdateUser() {
        User user = new User(FakerData.email(), FakerData.name(), FakerData.pwd());
        Response response = StepCrudUser.create(user);
        authResponsesForCleanup.add(response);
        Response updateUserData = StepCrudUser.updateUserDataUnauthorized(user);
        StepCrudUser.checkNotUpdateUser(updateUserData);
    }

    @Test
    @DisplayName("Нельзя изменить почту пользователя, указав почту которая уже есть в системе")
    @Description("PATCH /api/auth/user , возвращает 403 код, и ошибку в теле ответа")
    public void NotSuccessUpdateUserEmail() {
        User user = new User(FakerData.email(), FakerData.name(), FakerData.pwd());
        User user2 = new User(FakerData.email(), FakerData.name(), FakerData.pwd());
        User userUpdateData = new User(user.getEmail(), user2.getName(), user2.getPassword());

        Response response1 = StepCrudUser.create(user);
        authResponsesForCleanup.add(response1);
        Response response = StepCrudUser.create(user2);
        authResponsesForCleanup.add(response);

        Response updateUserData = StepCrudUser.updateAuthorizedUser(response, userUpdateData);
        StepCrudUser.checkNotUpdateUserEmail(updateUserData);
    }
}