package ru.tutor.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import net.datafaker.Faker;
import ru.tutor.api.RegisterApi;
import ru.tutor.base.BaseApiSteps;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.ResponseAnalyzer;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Шаги для тестов по ручке /register
 */

public class RegisterSteps extends BaseApiSteps {
    private final RegisterApi registerApi;
    private final Faker faker = new Faker(Locale.ENGLISH);

    public RegisterSteps(String baseUrl) {
        super(baseUrl);
        this.registerApi = new RegisterApi(baseUrl);
    }

    @Step("Регистрация пользователя с email: {email}")
    public Response registerUser(String email, String name, String password, String phone, String telegram, boolean approvePolicy) {
        Response response = registerApi.registerUser(email, name, password, phone, telegram, approvePolicy);
        ResponseAnalyzer.analyzeAndAttachResponse(response, "User Registration");
        return response;
    }

    @Step("Проверка успешной регистрации")
    public void validateSuccessfulRegistration(Response response) {
        assertSuccessStatusCode(response);
        assertContentTypeJson(response);

        String responseBody = response.getBody().asString();
        assertJsonFieldExists(response, "message");
        assertJsonFieldExists(response, "success");
        assertJsonFieldExists(response, "user_id");
        assertJsonFieldExists(response, "token");
        assertFieldNotExists(response, "error");
    }

    @Step("Проверка ошибки валидации")
    public void validateValidationError(Response response, int expectedStatus) {
        assertErrorStatusCode(response, expectedStatus);
        assertContentTypeJson(response);
        assertAnyFieldExists(response, "errors", "error", "message");
    }

    @Step("Проверка конфликта email: {email}")
    public void validateEmailConflict(Response response) {
        assertErrorStatusCode(response, 400);
        assertContentTypeJson(response);
        assertAnyFieldExists(response, "message", "error");
    }

    @Step("проверка обязательных заголовков")
    public void validateRequireHeaders(Response response) {
        assertContentTypeJson(response);
        assertHeaderExists(response, "Date");
    }

    @Step("Проверка времени ответа (максю {maxTimeMs} мс)")
    public void validateResponseTime(Response response, long maxTimeMs) {
        long responseTime = response.getTime();
        assertTrue(responseTime <= maxTimeMs,
                "Время ответа должно быть меньше " + maxTimeMs + "мсб но было: " + responseTime + "мс");
    }

    @Step("Проверка отсутствия чувствительных данных")
    public void validateNoSensitiveDate(Response response) {
        String responseBody = response.getBody().asString().toLowerCase();
        assertFalse(responseBody.contains("password"), "Ответ не должен содержать пароли");
        assertFalse(responseBody.contains("sql"), "Ответ не должен содержать SQL информацию");
        assertFalse(responseBody.contains("exception"), "Ответ не должен содержать исключения");
    }

    @Step("Генерация уникального номера телефона")
    public String generateUniquePhone() {
        return "+7" + faker.number().digits(10);
    }

    @Step("Прикрепление тестовых данных регистрации")
    public void attachRegistrationData(String email, String name, String phone, boolean approvePolicy) {
        AllureAttachments.attachText("Данные регистрации",
                "Email: " + email + "\n" +
                        "Name: " + name + "\n" +
                        "Phone: " + phone + "\n" +
                        "ApprovePolicy: " + approvePolicy);
    }

    @Step("Проверка, что код ответа = {expectedStatus}")
    public void assertStatus(Response response, int expectedStatus) {
        assertEquals(expectedStatus, response.getStatusCode(),
                "Ожидали статус " + expectedStatus + ", но получили " + response.getStatusCode());
    }

    @Step("Проверка, что сообщение ответа = {expectedMessage}")
    public void assertMessage(Response response, String expectedMessage) {
        String actualMessage = response.jsonPath().getString("message");
        assertEquals(expectedMessage, actualMessage,
                "Ожидали сообщение '" + expectedMessage + "', но получили '" + actualMessage + "'");
    }
}
