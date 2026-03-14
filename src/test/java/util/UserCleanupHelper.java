package util;

import io.restassured.response.Response;
import steps.StepCrudUser;
import java.util.List;

public class UserCleanupHelper {

    public static void cleanupCreatedUsers(List<Response> authResponses) {
        if (authResponses == null) return;
        for (Response authResponse : authResponses) {
            if (authResponse != null && authResponse.getStatusCode() == 200) {
                StepCrudUser.deleteUser(authResponse);
            }
        }
        authResponses.clear();
    }
}
