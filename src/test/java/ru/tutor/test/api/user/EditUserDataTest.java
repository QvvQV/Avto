package ru.tutor.test.api.user;

import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.config.TestConfig;
import ru.tutor.model.UserEditDataRequest;
import ru.tutor.steps.AccountSteps;
import ru.tutor.steps.AuthSteps;

import java.util.Locale;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(AllureJunit5.class)
@Epic("API Тесты пользователя")
@Feature("Управление профилем")
@DisplayName("Тесты изменения данных пользователя")
public class EditUserDataTest {

    private final AccountSteps userSteps = new AccountSteps(TestConfig.getCurrentApiUrl());
    private final AuthSteps authSteps = new AuthSteps(TestConfig.getCurrentApiUrl());
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();
    private static final Faker faker = new Faker(Locale.ENGLISH);

    private static String token;

    @BeforeAll
    public static void setUp() {
        Allure.label("component", "API");
        Allure.label("feature", "UserData");
    }

    @BeforeEach
    void setUpAuth() {
        token = step("Логин пользователя", () ->
                authSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD));
    }

    private UserEditDataRequest validRequest() {
        return new UserEditDataRequest()
                .email(TEST_EMAIL)
                .name(faker.name().maleFirstName())
                .telegram("@test")
                .phone(faker.phoneNumber().phoneNumber())
                .gender(0)
                .emailAdditional(faker.internet().emailAddress())
                .about(faker.lorem().sentence(10));
    }

    @Test
    @Order(1)
    @DisplayName("PUT /user/edit/data - успешное изменение данных")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("positive")
    void editDataSuccess() {
        Response response = step("PUT /user/edit/data", () ->
                userSteps.editUserData(token, validRequest())
        );
        step("Проверка успешного ответа", () ->
                userSteps.validateEditDataSuccess(response)
        );
    }

    @Test
    @Order(2)
    @DisplayName("PUT /user/edit/data - пустое имя")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void editDataEmptyName() {
        UserEditDataRequest body = validRequest().name("");
        Response response = step("PUT /user/edit/data с пустым name", () ->
                userSteps.editUserData(token, body)
        );
        step("Проверка ошибки", () -> {
            assertEquals(422, response.getStatusCode());
            String error = response.jsonPath().getString("error.messages.name");
            assertNotNull(error);
            assertEquals("Значение не должно быть пустым.", error);
        });
    }

    @Test
    @Order(3)
    @DisplayName("PUT /user/edit/data - невалидный email")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void editDataInvalidEmail() {
        UserEditDataRequest body = validRequest().email("not-an-email");
        Response response = step("PUT /user/edit/data с невалидным email", () ->
                userSteps.editUserData(token, body)
        );
        step("Проверка ошибки", () -> {
            assertEquals(422, response.getStatusCode());
            String error = response.jsonPath().getString("error.messages.email");
            assertNotNull(error);
            assertEquals("Значение адреса электронной почты недопустимо.", error);
        });
    }

    @Test
    @Order(4)
    @DisplayName("PUT /user/edit/data - без авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void editDataWithoutAuth() {
        Response response = step("PUT /user/edit/data без токена", () ->
                userSteps.editUserData("", validRequest())
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(5)
    @DisplayName("PUT /user/edit/data - невалидный токен")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void editDataInvalidToken() {
        Response response = step("PUT /user/edit/data невалидный токен", () ->
                userSteps.editUserData("invalid", validRequest())
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(6)
    @DisplayName("PUT /user/edit/data - битый валидный email")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void doesNotExistEditDataEmail() {
        UserEditDataRequest body = validRequest().email(TEST_EMAIL + "123456");
        Response response = step("PUT /user/edit/data с невалидным email", () ->
                userSteps.editUserData(token, body)
        );
        step("Проверка ошибки", () -> {
            assertEquals(400, response.getStatusCode());
            String error = response.jsonPath().getString("error");
            assertNotNull(error);
            assertEquals("ERR_FINDING_USER", error);
            String message = response.jsonPath().getString("message");
            assertNotNull(message);
            assertEquals("ERROR_RETRIEVING_USER_BY_EMAIL", message);
        });
    }
}
