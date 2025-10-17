package ru.tutor.test.api;

import io.qameta.allure.AllureId;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.api.Steps.ConfigSteps;
import ru.tutor.model.ConfigResponse;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AllureJunit5.class)
public class ConfigTest {
    private final ConfigSteps configSteps = new ConfigSteps(
            "https://ttplace.ru",
            "YWRtaW46Sld3cHBzRUE4NEI0Qm96SmdFNDRzTmlaWg==" // твой токен в base64
    );

//    @Test
//    @AllureId("45")
//    @DisplayName("GET /api/config - успешное получение конфигурации")
//    void getConfigSuccess() {
//        Response response = configSteps.getConfig();
//        assertEquals(200, response.statusCode());
//        assertNotNull(response.jsonPath().getString("requestPath"));
//    }
//
//    @Test
//    @AllureId("44")
//    @DisplayName("POST /api/config - попытка создать (ожидаем 405)")
//    void createConfigNotAllowed() {
//        String body = "{\"requestPath\":\"https://fake.ru\"}";
//        Response response = configSteps.createConfig(body);
//        assertEquals(405, response.statusCode());
//    }
//
//    @Test
//    @AllureId("43")
//    @DisplayName("PUT /api/config - попытка обновить (ожидаем 405)")
//    void updateConfigNotAllowed() {
//        String body = "{\"requestPath\":\"https://new.dev.ru\"}";
//        Response response = configSteps.updateConfig(body);
//        assertEquals(405, response.statusCode());
//    }
//
//    @Test
//    @AllureId("42")
//    @DisplayName("DELETE /api/config - попытка удалить (ожидаем 405)")
//    void deleteConfigNotAllowed() {
//        Response response = configSteps.deleteConfig();
//        assertEquals(405, response.statusCode());
//    }

    @Test
    @AllureId("45")
    @Order(1)
    @DisplayName("GET / api /config - успешное получение конфигурации")
    @Story("Чтение конфигурации")
    void getConfigSuccess() {
        ConfigResponse config = configSteps.validateSuccessfulConfigResponse();
        configSteps.validateConfigStructure(config);
        configSteps.analyzeConfig(config);
    }

    @Test
    @AllureId("44")
    @Order(2)
    @DisplayName("POST /api/config - метод не разрешен (405)")
    @Story("Неподдерживаемые методы")
    void createConfigNotAllowed() {
        String body = "{\"requestPath\":\"https://fake.ru\"}";
        Response response = configSteps.createConfig(body);
        configSteps.validateMethodNotAllowed(response);
        configSteps.analyze405Response(response, "POST");
    }

    @Test
    @AllureId("43")
    @Order(3)
    @DisplayName("PUT /api/config - метод не разрешён (405)")
    @Story("Неподдерживаемые методы")
    void updateConfigNotAllowed() {
        String body = "{\"requestPath\":\"https://new.dev.ru\"}";
        Response response = configSteps.updateConfig(body);
        configSteps.validateMethodNotAllowed(response);
        configSteps.analyze405Response(response, "PUT");
    }

    @Test
    @AllureId("42")
    @Order(4)
    @DisplayName("DELETE /api/config - метод не разрешён (405)")
    @Story("Неподдерживаемые методы")
    void deleteConfigNotAllowed() {
        Response response = configSteps.deleteConfig();
        configSteps.validateMethodNotAllowed(response);
        configSteps.analyze405Response(response, "DELETE");
    }

    @Test
    @DisplayName("Только GET метод должен быть разрешён для /api/config")
    @Story("Разрешённые методы")
    void onlyGetMethodShouldBeAllowed() {
        Response getResponse = configSteps.getConfig();
        assertEquals(200, getResponse.statusCode(), "GET method should return 200");

        String testBody = "{\"requestPath\":\"https://test.com\"}";

        Response postResponse = configSteps.createConfig(testBody);
        assertEquals(405, postResponse.statusCode(), "POST method should return 405");

        Response putResponse = configSteps.updateConfig(testBody);
        assertEquals(405, putResponse.statusCode(), "PUT method should return 405");

        Response deleteResponse = configSteps.deleteConfig();
        assertEquals(405, deleteResponse.statusCode(), "DELETE method should return 405");
    }

    @Nested
    @DisplayName("Тесты безопасности конфигурации")
    class SecurityTests {
        @Test
        @DisplayName("Успешный ответ должен содержать корректные заголовки")
        @Story("HTTP заголовки")
        void successfulResponseShouldHaveCorrectHeaders() {
            Response response = configSteps.getConfig();

            response.then()
                    .statusCode(200)
                    .contentType("application/json")
                    .header("Connection", not(emptyOrNullString()))
                    .header("Date", not(emptyOrNullString()));
        }
    }
}
