package ru.tutor.test.api.club;


import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.config.TestConfig;
import ru.tutor.flow.club.ClubDetailsFlow;
import ru.tutor.flow.club.TestDataFlow;
import ru.tutor.model.account.UserProfile;
import ru.tutor.steps.account.AuthSteps;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AllureJunit5.class)
@Epic("API Тесты пользователя")
@Feature("Детали самозанятого")
@DisplayName("Тесты эндпоинта Клуба: /user/club/sz-details + /user/club/details")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClubSzDetailsTest {

    // ==================== КОНСТАНТЫ ====================
    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private static final String TEST_EMAIL = TestConfig.getTestEmail();
    private static final String TEST_PASSWORD = TestConfig.getTestPassword();

    // ==================== ПОЛЯ ====================
    private AuthSteps authSteps = new AuthSteps(BASE_URL);
    private ClubDetailsFlow clubFlow;
    private TestDataFlow testDataFlow;
    private UserProfile profile;

    // ==================== НАСТРОЙКА ====================

    @BeforeEach
    public void setup() {

        profile = step("🔐 Авторизация пользователя", () ->
                authSteps.loginAndGetUserProfile(TEST_EMAIL, TEST_PASSWORD)
        );

        clubFlow = new ClubDetailsFlow(authSteps.getCurrentAuthToken(), BASE_URL);
        testDataFlow = new TestDataFlow(profile.getId(), profile.getEmail());

        logUserInfo();
    }

    private void logUserInfo() {
        Allure.addAttachment("👤 Пользователь", "text/plain",
                String.format("ID: %d\nEmail: %s", profile.getId(), profile.getEmail()));
    }

    @AfterEach
    public void tearDown() {
        clubFlow.deleteClubDetails();
    }

    // ==================== ТЕСТЫ ДЛЯ САМОЗАНЯТОГО ====================

    @Test
    @Order(1)
    @DisplayName("✅ СЗ: Сохранение валидных данных")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    void saveSelfEmployedDetails_Success() {
        var request = testDataFlow.generateValidSelfEmployedData();
        var result = clubFlow.saveSelfEmployedDetails(request);
        assertTrue(result.isSuccessful(), "Сохранение должно быть успешным");
        result.log();
    }

    @Test
    @Order(2)
    @DisplayName("❌ СЗ: Сохранение с минимальным набором полей")
    @Story("Валидация")
    @Severity(SeverityLevel.NORMAL)
    void saveSelfEmployedDetails_MinimalFields_ShouldFail() {
        var minimalPayload = testDataFlow.generateMinimalPayload();
        var result = clubFlow.saveWithMinimalFields(minimalPayload, "sz");
        assertTrue(result.isValidationError(), "Должна быть ошибка валидации");
        result.log();
    }

    @Test
    @Order(3)
    @DisplayName("❌ СЗ: Сохранение с невалидным email")
    @Story("Валидация")
    @Severity(SeverityLevel.NORMAL)
    void saveSelfEmployedDetails_InvalidEmail_ShouldFail() {
        var request = testDataFlow.generateDataWithInvalidEmail();
        var result = clubFlow.saveSelfEmployedDetails(request);
        assertTrue(result.isValidationError(), "Значение адреса электронной почты недопустимо.");
        result.log();
    }

    @Test
    @Order(4)
    @DisplayName("❌ СЗ: Сохранение с неверным userId")
    @Story("Валидация")
    @Severity(SeverityLevel.NORMAL)
    void saveSelfEmployedDetails_WrongUserId_ShouldFail() {
        var request = testDataFlow.generateDataWithWrongUserId(3338);
        var result = clubFlow.saveSelfEmployedDetails(request);
        assertFalse(result.isSuccessful(), "Сохранение с неверным userId должно завершиться ошибкой");
        result.log();
    }

    @Test
    @Order(5)
    @DisplayName("🔒 СЗ: Сохранение без авторизации")
    @Story("Безопасность")
    @Severity(SeverityLevel.CRITICAL)
    void saveSelfEmployedDetails_WithoutAuth_ShouldFail() {
        var unAuthFlow = new ClubDetailsFlow("invalid_token", BASE_URL);
        var request = testDataFlow.generateValidSelfEmployedData();
        var result = unAuthFlow.saveSelfEmployedDetails(request);
        assertTrue(result.isAuthError(), "Должна быть ошибка авторизации");
        result.log();
    }

    // ==================== ТЕСТЫ ДЛЯ ИП ====================
    @Test
    @Order(6)
    @DisplayName("✅ ИП: Сохранение валидных данных")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    void saveIpDetails_Success() {
        var request = testDataFlow.generateValidIpData();
        var result = clubFlow.saveIpDetails(request);
        assertTrue(result.isSuccessful(), "Сохранение должно быть успешным");
        result.log();
    }

    @Test
    @Order(7)
    @DisplayName("❌ ИП: Сохранение с неполными данными")
    @Story("Валидация")
    @Severity(SeverityLevel.NORMAL)
    void saveIpDetails_IncompleteData_ShouldFail() {
        var incompletePayload = testDataFlow.generateIncompleteIpPayload();
        var result = clubFlow.saveWithMinimalFields(incompletePayload, "ip");
        assertTrue(result.isValidationError(), "Ошибка валидации");
        result.log();
    }

    @Test
    @Order(8)
    @DisplayName("❌ ИП: Сохранение с невалидным ОГРНИП")
    @Story("Валидация")
    @Severity(SeverityLevel.NORMAL)
    void saveIpDetails_InvalidOgrnIp_ShouldFail() {
        var request = testDataFlow.generateDataWithInvalidOgrnip();
        var result = clubFlow.saveIpDetails(request);
        assertTrue(result.isValidationError(), "Ошибка валидации ОГРНИП");
        result.log();
    }

    // ==================== ТЕСТЫ ДЛЯ УДАЛЕНИЯ ====================

    @Test
    @Order(9)
    @DisplayName("✅ DELETE: Успешное удаление")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    void deleteClubDetails_Success() {
        var createRequest = testDataFlow.generateValidSelfEmployedData();
        var createResult = clubFlow.saveSelfEmployedDetails(createRequest);
        assertTrue(createResult.isSuccessful(), "Успешное создание данных");
        var deleteResult = clubFlow.deleteClubDetails();
        assertTrue(deleteResult.isSuccessful(), "Успешное удаление");
        deleteResult.log();
    }

    @Test
    @Order(10)
    @DisplayName("🔄 DELETE: Проверка идемпотентности")
    @Story("Функциональность")
    @Severity(SeverityLevel.NORMAL)
    void deleteClubDetails_Idempotence() {
        var createRequest = testDataFlow.generateValidSelfEmployedData();
        var createResult = clubFlow.saveSelfEmployedDetails(createRequest);
        assertTrue(createResult.isSuccessful(), "Успешное создание данных");
        var idempotenceResult = clubFlow.testDeleteIdempotence(3);
        assertTrue(idempotenceResult.isIdempotent());
        idempotenceResult.log();
    }

    @Test
    @Order(11)
    @DisplayName("🔒 DELETE: Без авторизации")
    @Story("Безопасность")
    @Severity(SeverityLevel.CRITICAL)
    void deleteClubDetails_WithoutAuth_ShouldFail() {
        var unAuthFlow = new ClubDetailsFlow("invalid_token", BASE_URL);
        var result = unAuthFlow.deleteClubDetails();
        assertTrue(result.isAuthError() || result.isNotFound(),
                "Должна быть ошибка авторизации");
        assertEquals(400, clubFlow.deleteClubDetails().getStatusCode());
        result.log();
    }

    // ==================== СМОК-ТЕСТЫ ====================

    @Test
    @Order(12)
    @DisplayName("🚀 Смок-тест: Полный цикл СЗ")
    @Story("Smoke test")
    @Severity(SeverityLevel.BLOCKER)
    void smokeTest_SelfEmployedLifecycle() {
        var request = testDataFlow.generateValidSelfEmployedData();
        var lifecycle = clubFlow.executeFullLifecycle(request, "sz");
        assertTrue(lifecycle.isSuccess());
        lifecycle.log();
    }

    @Test
    @Order(13)
    @DisplayName("🚀 Смок-тест: Полный цикл ИП")
    @Story("Smoke test")
    @Severity(SeverityLevel.BLOCKER)
    void smokeTest_IpLifecycle() {
        var request = testDataFlow.generateValidIpData();
        var lifecycle = clubFlow.executeFullLifecycle(request, "ip");
        assertTrue(lifecycle.isSuccess());
        lifecycle.log();
    }

    // ==================== МАССОВЫЕ ТЕСТЫ ====================

    @Test
    @Order(14)
    @DisplayName("📊 Массовое создание и удаление")
    @Story("Нагрузочное тестирование")
    @Severity(SeverityLevel.NORMAL)
    void multipleCreateAndDelete() {
        int iterations = 3;

        for (int i = 1; i <= iterations; i++) {
            int finalI = i;
            Allure.step(String.format("Итерация #%d", i), ()-> {
                var request = testDataFlow.generateValidSelfEmployedData();
                var result = clubFlow.saveSelfEmployedDetails(request);
                assertTrue(result.isSuccessful(), "Создание #" + finalI);

                var deleteResult = clubFlow.deleteClubDetails();
                assertTrue(deleteResult.isSuccessful(), "Удаление #" + finalI);

                Allure.addAttachment(String.format("Итерация #%d", finalI), "text/plain",
                        String.format("✅ Создание: %d\n✅ Удаление: %d",
                                result.getStatusCode(),
                                deleteResult.getStatusCode()));
            });
        }

        Allure.addAttachment("📊 Итог", "text/plain",
                String.format("Успешно выполнено %d полных циклов", iterations));
    }
 }
//
//    TODO: Код ответа не соответсвует принципам REST
//     @Order(10)
//     @DisplayName("Проверка идемпотентности DELETE")
//     После первого удаления
//     последующие должны вовзращать 404, фактически 400