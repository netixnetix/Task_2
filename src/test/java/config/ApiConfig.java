package config;
import io.restassured.RestAssured;

public class ApiConfig {

    private static final String BASE_URL = "https://stellarburgers.education-services.ru/";

    public static void setUp(){
        RestAssured.baseURI = BASE_URL;
    }
}