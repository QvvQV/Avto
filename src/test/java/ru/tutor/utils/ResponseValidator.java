package ru.tutor.utils;

import io.restassured.response.Response;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class ResponseValidator {

    private ResponseValidator() {}

    public static void validateSuccessResponse(Response response) {
        response.then()
                .statusCode(200)
                .contentType("application/json")
                .header("Content-Type", containsString("application/json"))
                .body("$", hasKey("token"))
                .body("token", notNullValue())
                .body("token", not(emptyString()));
    }

    public static void validateErrorResponse(Response response, int expectedStatusCode) {
        response.then()
                .statusCode(expectedStatusCode)
                .contentType("application/json")
                .body("$", not(hasKey("token")))
                .body("$", anyOf(hasKey("error"), hasKey("message")));
    }

    public static void validateResponseTime(Response response, long maxTimeMs) {
        long responseTime = response.getTime();
        assertTrue(responseTime <= maxTimeMs,
                  "Response time should be less than " + maxTimeMs + "ms, but was: " + responseTime);
    }

    public static void validateNoSensitiveData(Response response, String... sensitiveStrings) {
        String responseBody = response.getBody().asString().toLowerCase();

        for (String sensitive : sensitiveStrings) {
            assertFalse(responseBody.contains(sensitive.toLowerCase()),
                      "Response should not contain sensitive data: " + sensitive);
        }

        // Common sensitive data checks
        assertFalse(responseBody.contains("password"));
        assertFalse(responseBody.contains("secret"));
        assertFalse(responseBody.contains("key"));
        assertFalse(responseBody.contains("sql exception"));
    }
}
