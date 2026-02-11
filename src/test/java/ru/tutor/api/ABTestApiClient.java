package ru.tutor.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.json.JSONObject;

import static io.restassured.RestAssured.given;

public class ABTestApiClient {

    private static final String BASE_URL = "https://api.tutorplace.ru";

    @Step("Получить конфигурацию A/B теста для landingId = {landingId}")
    public ABTestConfig getABTestConfig(int landingId) {
        Response response = given()
                .when()
                .get(BASE_URL + "/landing/ab-test-config/" + landingId)
                .then()
                .statusCode(200)
                .extract().response();

        JSONObject json = new JSONObject(response.asString());
        return new ABTestConfig(
                json.getBoolean("isActive"),
                json.getInt("chanceA"),
                json.getInt("chanceB"),
                json.getInt("version")
        );
    }

    @Step("Отправить событие в A/B тест")
    public Response sendABTestEvent(String eventType, int formVersion, String sessionId) {
        JSONObject payload = new JSONObject();
        payload.put("formVersion", formVersion);
        payload.put("domain", "matrixbf5.tutorplace.ru");
        payload.put("sessionId", sessionId);
        payload.put("event", eventType);

        return given()
                .header("Content-Type", "application/json")
                .body(payload.toString())
                .when()
                .post(BASE_URL + "/landing/event");
    }

    public static class ABTestConfig {
        private final boolean isActive;
        private final int chanceA;
        private final int chanceB;
        private final int version;

        public ABTestConfig(boolean isActive, int chanceA, int chanceB, int version) {
            this.isActive = isActive;
            this.chanceA = chanceA;
            this.chanceB = chanceB;
            this.version = version;
        }

        public boolean isActive() { return isActive; }
        public int getChanceA() { return chanceA; }
        public int getChanceB() { return chanceB; }
        public int getVersion() { return version; }

        @Override
        public String toString() {
            return String.format("ABTestConfig{isActive=%s, chanceA=%d%%, chanceB=%d%%, version=%d}",
                    isActive, chanceA, chanceB, version);
        }
    }
}
