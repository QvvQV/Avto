package ru.tutor.test.api.user;

import io.qameta.allure.*;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.junit.jupiter.api.*;
import ru.tutor.config.TestConfig;
import ru.tutor.flow.account.FreshUserFixture;
import ru.tutor.model.account.UserEditDataRequest;
import ru.tutor.steps.account.AccountSteps;

import java.util.Locale;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import ru.tutor.constants.Tags;
import ru.tutor.steps.account.ProfileSteps;

@Epic("API Тесты пользователя")
@Feature("Редактирование профилем")
@DisplayName("Редактирование данных пользователя")
@Tag(Tags.INTEGRATION)
@Tag(Tags.API)
public class EditUserDataTest {

    private final AccountSteps userSteps = new AccountSteps(TestConfig.getCurrentApiUrl());
    private final ProfileSteps profileSteps = new ProfileSteps(TestConfig.getCurrentApiUrl());
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private static final Faker faker = new Faker(Locale.ENGLISH);

    private final FreshUserFixture freshUserFixture = new FreshUserFixture();
    private static String freshEmail;

    private static String token;

    @BeforeAll
    public static void setUp() {
        Allure.label("component", "API");
        Allure.label("feature", "UserData");
    }

    @BeforeEach
    void setUpAuth() {
        FreshUserFixture.FreshUser freshUser = step("Регистрация нового пользователя (POST /register)",
                freshUserFixture::register);
        token = freshUser.token();
        freshEmail = freshUser.email();
    }

    private UserEditDataRequest validRequest() {
        return new UserEditDataRequest()
                .email(freshEmail)
                .name(faker.name().maleFirstName())
                .telegram("@test")
                .phone(faker.phoneNumber().phoneNumber())
                .gender(0)
                .emailAdditional(faker.internet().emailAddress())
                .about(faker.lorem().sentence(10));
    }

    @Test
    @DisplayName("PUT /user/edit/data - успешное изменение данных")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    void editDataSuccess() {
        UserEditDataRequest body = validRequest();
        Response response = step("PUT /user/edit/data", () ->
                userSteps.editUserData(token, body)
        );
        step("Проверка успешного ответа", () ->
                userSteps.validateEditDataSuccess(response)
        );
        step("Read-back: GET /user/profile -> данные применились", () -> {
            Response profile = profileSteps.getProfile(token);
            assertEquals(body.getName(), profile.jsonPath().getString("name"), "name должен примениться");
            assertEquals(body.getPhone(), profile.jsonPath().getString("phone"), "phone должен примениться");
            assertEquals(body.getAbout(), profile.jsonPath().getString("about"), "about должен примениться");
            assertEquals(body.getEmailAdditional(), profile.jsonPath().getString("emailAdditional"), "emailAdditional должен примениться");
            assertEquals(freshEmail, profile.jsonPath().getString("email"), "email изменён быть не должен");
        });
    }

    @Test
    @DisplayName("PUT /user/edit/data - пустое имя")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
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
    @DisplayName("PUT /user/edit/data - невалидный email")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
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
    @DisplayName("PUT /user/edit/data - без авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void editDataWithoutAuth() {
        Response response = step("PUT /user/edit/data без токена", () ->
                userSteps.editUserData("", validRequest())
        );
        step("Проверка статуса 401", () ->
                userSteps.assertUnauthorizedWithMessage(response, "JWT Token not found"));
    }

    @Test
    @DisplayName("PUT /user/edit/data - невалидный токен")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void editDataInvalidToken() {
        Response response = step("PUT /user/edit/data невалидный токен", () ->
                userSteps.editUserData("invalid", validRequest())
        );
        step("Проверка статуса 401", () ->
                userSteps.assertUnauthorizedWithMessage(response, "Invalid JWT Token"));
    }

    @Test
    @DisplayName("PUT /user/edit/data - битый валидный email")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
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
