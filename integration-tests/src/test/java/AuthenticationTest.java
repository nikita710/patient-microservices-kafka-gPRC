import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.notNullValue;

public class AuthenticationTest {
    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost:4004";
    }

    @Test
    public void shouldReturnUnauthorizedWithInvalidToken() {
        //1. Arrange
        //2. Act
        //3. Assert

        String loginPayload = """
                    {
                        "email": "wrong_email@test.com",
                        "password": "wrong_password"
                    }
                """;
        RestAssured.given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401);
    }

    @Test
    public void shouldReturnOKWithValidToken() {
        //1. Arrange
        //2. Act
        //3. Assert

        String loginPayload = """
                {
                    "email": "testuser@test.com",
                    "password": "password123"
                }
                """;

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .extract()
                .response();

        System.out.println("Token: " + response.jsonPath().getString("token"));
    }

}
