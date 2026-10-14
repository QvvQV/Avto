package ru.tutor.test.api.subscription;

import io.qameta.allure.*;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.tutor.config.TestConfig;
import ru.tutor.steps.account.ResetSteps;

import java.util.Locale;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

import ru.tutor.constants.Tags;

@Epic("Восстановить Пароль")
@Feature("Сброс пароля")
@DisplayName("Тесты эндпоинта POST /reset")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag(Tags.INTEGRATION)
public class ResetTest {
    private final ResetSteps resetSteps = new ResetSteps(TestConfig.getCurrentApiUrl());
    private static final Faker faker = new Faker(Locale.ENGLISH);

    private static final String RESET_ENDPOINT = "POST /reset";
    private static final String EXISTING_EMAIL = "lip@test.ru";

    @Test
    @Order(1)
    @DisplayName("Сброс пароля с валидным email")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.CONTRACT)
    void resetWithValidEmail() {
        Response response = step(RESET_ENDPOINT + " с валидным email", () ->
                resetSteps.requestPasswordReset(EXISTING_EMAIL)
        );

        step("Проверка статуса 200", () ->
            assertEquals(200, response.getStatusCode())
        );
    }

    @Test
    @Order(2)
    @DisplayName("Сброс пароля с несуществующим email")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void resetWithUnknownEmail() {
        Response response = step(RESET_ENDPOINT + " с несуществующим email", () ->
                resetSteps.requestPasswordReset(faker.internet().emailAddress())
        );

        step("Проверка статуса 400 и сообщения", () -> {
            assertEquals(400, response.getStatusCode());
            assertEquals("ERR_RESET_PASSWORD", response.jsonPath().getString("error"));
            assertEquals("User not found", response.jsonPath().getString("message"));
        });

        step("Проверить отсутствие чувствительных данных", () -> {
            String responseBody = response.getBody().asString().toLowerCase();
            assertFalse(responseBody.contains("12345678"));
            assertFalse(responseBody.contains("secret"));
        });
    }

    @Test
    @Order(3)
    @DisplayName("Сброс пароля с пустым email")
    @Story("Валидация")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void resetWithEmptyEmail() {
        Response response = step(RESET_ENDPOINT + " с пустым email", () ->
                resetSteps.requestPasswordReset("")
        );

        step("Проверка статуса 422", () -> {
            assertEquals(422, response.getStatusCode());
            assertEquals("Значение не должно быть пустым.", response.jsonPath().getString("error.messages.email"));
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid-email",
            "test@",
            "@domain.com",
            "test@domain",
            "test@domain."
    })
    @Order(4)
    @DisplayName("Сброс пароля с невалидными email форматами")
    @Severity(SeverityLevel.NORMAL)
    @Story("Валидация")
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void resetWithInvalidEmailFormats(String invalidEmail) {
        Response response = step( RESET_ENDPOINT + " с невалидным email: " + invalidEmail, () ->
                resetSteps.requestPasswordReset(invalidEmail)
        );

        step("Проверить ошибку валидации для: " + invalidEmail, () -> {
            assertEquals(422, response.getStatusCode());
            assertEquals("Значение адреса электронной почты недопустимо.", response.jsonPath().getString("error.messages.email"));
        });
    }

    @Test
    @Order(5)
    @DisplayName("Сброс пароля с email в верхнем регистре")
    @Story("Нормализация")
    @Severity(SeverityLevel.MINOR)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.CONTRACT)
    void resetWithUppercaseEmail() {
        Response response = step(RESET_ENDPOINT + " с email в верхнем регистре", () ->
                resetSteps.requestPasswordReset(EXISTING_EMAIL.toUpperCase(Locale.ROOT))
        );

        step("Проверка статуса 200 (нормализация регистра)", () -> {
            assertEquals(200, response.getStatusCode());
        });
    }

    @Test
    @Order(6)
    @DisplayName("Сброс пароля с email содержащим спецсимволы")
    @Story("Негативный сценарий")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void resetWithSpecialCharsEmail() {
        Response response = step( RESET_ENDPOINT + " с email содержащим спецсимволы", () ->
                resetSteps.requestPasswordReset("test+tag@domain.com")
        );

        step("Проверка статуса 400 и сообщения", () -> {
            assertEquals(400, response.getStatusCode());
            assertEquals("ERR_RESET_PASSWORD", response.jsonPath().getString("error"));
            assertEquals("User not found", response.jsonPath().getString("message"));
        });
    }

    @Test
    @Order(7)
    @DisplayName("Повторный сброс пароля")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.MINOR)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.CONTRACT)
    void resetPasswordTwice() {
        Response first = step("Первый " + RESET_ENDPOINT, () -> resetSteps.requestPasswordReset(EXISTING_EMAIL));

        step("Проверка статуса первого запроса", () ->
                assertEquals(200, first.getStatusCode()));

        Response second = step("Второй " + RESET_ENDPOINT, () -> resetSteps.requestPasswordReset(EXISTING_EMAIL));

        step("Проверка статуса второго запроса 200", () ->
                assertEquals(200, second.getStatusCode()));
    }
}