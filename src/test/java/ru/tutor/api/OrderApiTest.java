package ru.tutor.api;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import ru.tutor.config.TestConfig;
import ru.tutor.utils.AllureAttachments;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.sleep;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;


// Получение токен для авторов TP-5735 Регистрация для авторов
@Epic("API Tests")
@Feature("Создание заказа")
@DisplayName("Тесты для эндпоинта создания заказа")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderApiTest {

    private static final String CREATE_ORDER_ENDPOINT = "/order";
    private String testEmail;
    private Map<String, String> formData;

    @BeforeAll
    void setUp() {
        RestAssured.baseURI = TestConfig.getCurrentUiUrl();
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

        // Генерация уникального email один раз для всех тестов
        testEmail = "tes" + System.currentTimeMillis() + "@as.qq";
    }

    @BeforeEach
    void prepareTestData() {
        // Подготовка данных формы перед каждым тестом
        formData = new HashMap<>();
        formData.put("isAuthorRegister", "true");
        formData.put("order[email]", testEmail);
        formData.put("order[name]", "Created Name");

        AllureAttachments.attachText("Тестовые данные",
                "Email: " + testEmail + "\nName: Created Name");
    }

    @Test
    @DisplayName("Успешное создание заказа с автором и получение токена")
    void createOrderWithAuthor_ShouldReturnToken() {
        // 1. Отправка запроса и получение ответа
        Response response = sendCreateOrderRequest(formData);

        // 2. Проверка статуса и структуры ответа
        validateResponseStatusAndSchema(response);

        // 3. Извлечение и валидация токена
        String loginTokenUrl = extractAndValidateToken(response);

        // 4. Дополнительная проверка содержимого токена
        validateTokenContent(loginTokenUrl);

//        openTokenUrlInBrowser(loginTokenUrl);
    }

    @Step("Открыть URL токена в браузере")
    private void openTokenUrlInBrowser(String tokenUrl) {
        // Настройка браузера
        Configuration.browser = "chrome";
        Configuration.timeout = 10000;

        // Открываем URL с токеном
        open(tokenUrl);

        // Ждем загрузки
        sleep(3000);

        AllureAttachments.attachScreenshot("Страница после открытия токена");
        AllureAttachments.attachText("Текущий URL", WebDriverRunner.url());
    }

    @Step("Отправка POST запроса на создание заказа")
    private Response sendCreateOrderRequest(Map<String, String> formData) {
        // Прикрепляем данные запроса к отчету
        StringBuilder requestDetails = new StringBuilder();
        requestDetails.append("URL: ").append(RestAssured.baseURI).append(CREATE_ORDER_ENDPOINT).append("\n");
        requestDetails.append("Method: POST\n");
        requestDetails.append("Content-Type: multipart/form-data\n");
        requestDetails.append("Form Data:\n");
        formData.forEach((key, value) -> requestDetails.append("  ").append(key).append(": ").append(value).append("\n"));
        AllureAttachments.attachText("Детали запроса", requestDetails.toString());

         return given()
                .contentType(ContentType.MULTIPART)
                .multiPart("isAuthorRegister", formData.get("isAuthorRegister"))
                .multiPart("order[email]", formData.get("order[email]"))
                .multiPart("order[name]", formData.get("order[name]"))
                .when()
                .post(CREATE_ORDER_ENDPOINT)
                .then()
                .extract()
                .response();

//        AllureAttachments.attachApiResponse("Ответ на создание заказа", response);
    }

    @Step("Валидация статуса и JSON-схемы ответа")
    private void validateResponseStatusAndSchema(Response response) {
        response.then()
                .assertThat()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasKey("token"))
                .body("token", notNullValue())
                .body("token", containsString(TestConfig.getCurrentClientDomain()));
    }

    @Step("Извлечение и валидация токена из ответа")
    private String extractAndValidateToken(Response response) {
        JsonPath jsonPath = response.jsonPath();
        String tokenUrl = jsonPath.getString("token");

        assertNotNull(tokenUrl, "В ответе отсутствует ключ 'token' или его значение равно null");
        assertTrue(tokenUrl.startsWith("https://"), "Токен должен быть валидным URL");
        assertTrue(tokenUrl.contains("login_token="), "URL должен содержать login_token");

        AllureAttachments.attachText("Полученный токен URL", tokenUrl);
        return tokenUrl;
    }

    @Step("Проверка содержимого токена (URL)")
    private void validateTokenContent(String tokenUrl) {
        // Извлекаем отдельные компоненты токена
        String username = extractQueryParam(tokenUrl, "username");
        String loginToken = extractQueryParam(tokenUrl, "login_token");

        assertAll("Проверка параметров токена",
                () -> assertNotNull(username, "username не должен быть null"),
                () -> assertNotNull(loginToken, "login_token не должен быть null"),
                () -> {
                    assert loginToken != null;
                    assertTrue(loginToken.length() >= 32, "login_token должен быть достаточно длинным");
                }
        );

        AllureAttachments.attachText("Детали токена",
                "Username: " + username + "\n" +
                        "Login Token: " + loginToken + "\n" +
                        "Полный URL: " + tokenUrl);
    }

    @Step("Извлечение параметра из query string")
    private String extractQueryParam(String url, String paramName) {
        try {
            String query = url.substring(url.indexOf("?") + 1);
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                if (idx > 0 && pair.substring(0, idx).equals(paramName)) {
                    return pair.substring(idx + 1);
                }
            }
        } catch (Exception e) {
            // Если не удалось извлечь параметр
        }
        return null;
    }

//    @Test
    @DisplayName("Создание заказа без автора (isAuthorRegister=false)")
    void createOrderWithoutAuthor_ShouldSucceed() {
        // Модифицируем данные для этого конкретного теста
        Map<String, String> testFormData = new HashMap<>(formData);
        testFormData.put("isAuthorRegister", "false");
        testFormData.put("order[email]", "guest" + System.currentTimeMillis() + "@as.qq");

        Response response = sendCreateOrderRequest(testFormData);
        validateResponseStatusAndSchema(response);
    }

//    @Test
    @DisplayName("Попытка создания заказа с некорректным email")
    void createOrderWithInvalidEmail_ShouldReturnError() {
        Map<String, String> testFormData = new HashMap<>();
        testFormData.put("isAuthorRegister", "true");
        testFormData.put("order[email]", "invalid-email");
        testFormData.put("order[name]", "Test Name");

        Response response = given()
                .contentType(ContentType.MULTIPART)
                .multiPart("isAuthorRegister", testFormData.get("isAuthorRegister"))
                .multiPart("order[email]", testFormData.get("order[email]"))
                .multiPart("order[name]", testFormData.get("order[name]"))
                .when()
                .post(CREATE_ORDER_ENDPOINT);

        response.then().assertThat().statusCode(400);
//        AllureAttachments.attachApiResponse("Ответ с ошибкой валидации", response);
    }

//    @Test
    @DisplayName("Создание заказа с пустым именем")
    void createOrderWithEmptyName_ShouldReturnError() {
        Map<String, String> testFormData = new HashMap<>(formData);
        testFormData.put("order[name]", "");

        Response response = given()
                .contentType(ContentType.MULTIPART)
                .multiPart("isAuthorRegister", testFormData.get("isAuthorRegister"))
                .multiPart("order[email]", testFormData.get("order[email]"))
                .multiPart("order[name]", testFormData.get("order[name]"))
                .when()
                .post(CREATE_ORDER_ENDPOINT);

        response.then().assertThat().statusCode(400);
//        AllureAttachments.attachApiResponse("Ответ с ошибкой валидации", response);
    }

    @AfterEach
    void logTestCompletion() {
        AllureAttachments.attachText("Завершение теста",
                "Тест завершен: " + LocalDateTime.now() + "\n" +
                        "Использованный email: " + testEmail);
    }
}
