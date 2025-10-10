package ru.tutor.test.api;

import io.qameta.allure.AllureId;
import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.api.Steps.ConfigSteps;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AllureJunit5.class)
public class ConfigTest {
    private final ConfigSteps configSteps = new ConfigSteps(
            "https://ttplace.ru",
            "YWRtaW46Sld3cHBzRUE4NEI0Qm96SmdFNDRzTmlaWg==" // твой токен в base64
    );

    @Test
    @AllureId("45")
    @DisplayName("GET /api/config - успешное получение конфигурации")
    void getConfigSuccess() {
        Response response = configSteps.getConfig();
        assertEquals(200, response.statusCode());
        assertNotNull(response.jsonPath().getString("requestPath"));
    }

    @Test
    @AllureId("44")
    @DisplayName("POST /api/config - попытка создать (ожидаем 405)")
    void createConfigNotAllowed() {
        String body = "{\"requestPath\":\"https://fake.ru\"}";
        Response response = configSteps.createConfig(body);
        assertEquals(405, response.statusCode());
    }

    @Test
    @AllureId("43")
    @DisplayName("PUT /api/config - попытка обновить (ожидаем 405)")
    void updateConfigNotAllowed() {
        String body = "{\"requestPath\":\"https://new.dev.ru\"}";
        Response response = configSteps.updateConfig(body);
        assertEquals(405, response.statusCode());
    }

    @Test
    @AllureId("42")
    @DisplayName("DELETE /api/config - попытка удалить (ожидаем 405)")
    void deleteConfigNotAllowed() {
        Response response = configSteps.deleteConfig();
        assertEquals(405, response.statusCode());
    }
}
