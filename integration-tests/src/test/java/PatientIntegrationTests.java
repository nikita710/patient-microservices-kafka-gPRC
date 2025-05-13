import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.notNullValue;

class PatientIntegrationTests {
    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost:4004";
    }

    @Test
    void shouldReturnPatientWithValidToken() {
        //1. Arrange
        //2. Act
        //3. Assert

        String loginPayload = """
                    {
                        "email": "testuser@test.com",
                        "password": "password123"
                    }
                """;
        String token = RestAssured.given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");

        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/patient/allPatients")
                .then()
                .statusCode(200)
                .body("patient", notNullValue());
    }
}
