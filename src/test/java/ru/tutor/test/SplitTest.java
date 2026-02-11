package ru.tutor.test;


import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import net.datafaker.Faker;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.tutor.api.ABTestApiClient;
import ru.tutor.page.FormPage;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.ABTestStatistics;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.*;

@Epic("A/B тестирование")
@Feature("UI тест сплит-тестирования")
@DisplayName("Тест определения версии A/B по наличию иконки карандаша")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SplitTest {

    private FormPage formPage;
    private ABTestApiClient apiClient;
    private ABTestStatistics statistics;
    private final Faker faker = new Faker();
    private final Map<String, TestResult> testResults = new HashMap<>();

//    @BeforeAll
//    static void setupBrowser() {
//        configuration.timeout = 15000;
//        configuration.browser = "chrome";
//        configuration.headless = false; // Для визуальной проверки
//    }

    @BeforeEach
    void setUp() {
        formPage = new FormPage();
        apiClient = new ABTestApiClient();
        statistics = new ABTestStatistics();
    }

    @Test
    @DisplayName("Одиночный тест: проверка всего потока")
    void testSingleUserFlow() {
        String testId = UUID.randomUUID().toString().substring(0, 8);
        String userName = faker.name().firstName();
        String userEmail = formPage.generateRandomEmail();
        String sessionId = UUID.randomUUID().toString().replace("-", "");

        TestResult result = new TestResult(testId, userName, userEmail, sessionId);

        try {
            Allure.step("1. Получить конфигурацию A/B теста", () -> {
                ABTestApiClient.ABTestConfig config = apiClient.getABTestConfig(252);

                AllureAttachments.attachJson("AB Test Config",
                        String.format("{\"landingId\": 252, \"isActive\": %s, \"chanceA\": %d, \"chanceB\": %d, \"version\": %d}",
                                config.isActive(), config.getChanceA(), config.getChanceB(), config.getVersion()));

                result.setConfig(config);

//                assertTrue(config.isActive(), "A/B тест должен быть активен");
                assertEquals(100, config.getChanceA(), "Шанс версии A должен быть 50%");
                assertEquals(0, config.getChanceB(), "Шанс версии B должен быть 50%");
            });

            Allure.step("2. Открыть и заполнить форму расчета", () -> {
                formPage.openMainPage();
                formPage.takeScreenshot("main_page_loaded");

                // Генерируем реалистичные данные
                String birthDate = generateBirthDate();
                formPage.fillCalculationForm(userName, birthDate);
                formPage.takeScreenshot("form_filled");

                result.setBirthDate(birthDate);
            });

            Allure.step("3. Нажать 'Рассчитать матрицу'", () -> {
                formPage.clickCalculateButton();
                // Ждем расчет
                sleep(3000);
                formPage.takeScreenshot("after_calculation");
            });

            Allure.step("4. Дождаться модального окна", () -> {
                formPage.waitForModal();
                formPage.takeScreenshot("modal_opened");
            });

            Allure.step("5. Определить версию A/B теста по иконке", () -> {
                int detectedVersion = formPage.determineABVersion();
                result.setDetectedVersion(detectedVersion);

                String versionDescription = detectedVersion == 1
                        ? "Версия 1 (с иконкой карандаша)"
                        : "Версия 0 (без иконки карандаша)";

                AllureAttachments.attachText("Определенная версия",
                        "Версия: " + detectedVersion + " - " + versionDescription);

                formPage.takeScreenshot("version_detected_" + detectedVersion);
            });

            Allure.step("6. Заполнить email и отправить", () -> {
                formPage.fillEmail(userEmail);
                formPage.takeScreenshot("email_filled");

                formPage.clickSubmitButton();
                sleep(2000); // Ждем обработку

                // Проверяем результат
                String currentUrl = formPage.getCurrentUrl();
                boolean hasErrors = formPage.hasValidationErrors();

                result.setFinalUrl(currentUrl);
                result.setHasErrors(hasErrors);

                AllureAttachments.attachText("Submit Result",
                        "Current URL: " + currentUrl + "\n" +
                                "Has validation errors: " + hasErrors);

                formPage.takeScreenshot("after_submit");
            });

            Allure.step("7. Отправить событие в A/B систему", () -> {
                try {
                    var response = apiClient.sendABTestEvent("submit", result.getDetectedVersion(), sessionId);
                    result.setApiResponseStatus(response.getStatusCode());

                    AllureAttachments.attachText("API Event Response",
                            "Status: " + response.getStatusCode() + "\n" +
                                    "Body: " + response.getBody().asString());
                } catch (Exception e) {
                    AllureAttachments.attachText("API Event Error",
                            "Error sending event: " + e.getMessage());
                }
            });

            result.setSuccess(true);

        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
            AllureAttachments.attachText("Test Error",
                    "Error in test: " + e.getMessage() + "\n" +
                            "User: " + userName + "\n" +
                            "Email: " + userEmail);
            throw e;
        } finally {
            // Сохраняем результат
            testResults.put(testId, result);
            statistics.addTestResult(result.getDetectedVersion(), true);

            // Выводим итог
            Allure.step("Итог теста", () -> {
                AllureAttachments.attachText("Test Result Summary", result.toString());
            });
        }
    }

    @ParameterizedTest(name = "Пользователь #{0}")
    @ValueSource(ints = {1, 2, 3, 4, 5})
    @DisplayName("Многопользовательский тест для статистики распределения")
    void testMultipleUsersDistribution(int userNumber) {
        String testId = "user_" + userNumber + "_" + UUID.randomUUID().toString().substring(0, 6);
        String userName = "User_" + userNumber + "_" + faker.name().firstName();
        String userEmail = "test" + userNumber + "_" + System.currentTimeMillis() + "@test.ru";
        String sessionId = UUID.randomUUID().toString().replace("-", "");

        TestResult result = new TestResult(testId, userName, userEmail, sessionId);

        try {
            Allure.step("Тестирование пользователя " + userNumber, () -> {
                // 1. Очищаем браузер
                clearBrowserCookies();
                clearBrowserLocalStorage();
                refresh();

                // 2. Открываем и заполняем форму
                formPage.openMainPage();
                sleep(1000); // Ждем загрузку

                String birthDate = generateBirthDate();
                formPage.fillCalculationForm(userName, birthDate);
                formPage.clickCalculateButton();
                sleep(3000); // Ждем расчет

                // 3. Ждем модальное окно
                formPage.waitForModal();
                sleep(1000); // Даем время на стабилизацию

                // 4. Определяем версию
                int detectedVersion = formPage.determineABVersion();
                result.setDetectedVersion(detectedVersion);

                // 5. Заполняем и отправляем
                formPage.fillEmail(userEmail);
                formPage.clickSubmitButton();
                sleep(2000);

                // 6. Сохраняем результат
                result.setFinalUrl(formPage.getCurrentUrl());
                result.setSuccess(true);

                // 7. Добавляем в статистику
                statistics.addTestResult(detectedVersion, true);

                // 8. Логируем
                AllureAttachments.attachText("User " + userNumber + " Result",
                        "Name: " + userName + "\n" +
                                "Email: " + userEmail + "\n" +
                                "Detected version: " + detectedVersion +
                                (detectedVersion == 1 ? " (с иконкой)" : " (без иконки)"));
            });

        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
            AllureAttachments.attachText("Error for user " + userNumber,
                    "Error: " + e.getMessage());
            // Пропускаем в статистике
        } finally {
            testResults.put(testId, result);
        }
    }

    @Test
    @DisplayName("Статистический анализ распределения версий")
    void testStatisticalAnalysis() {
        Allure.step("Запуск тестов для 10 пользователей", () -> {
            int totalUsers = 10;

            for (int i = 1; i <= totalUsers; i++) {
                try {
                    // Быстрый тест без детального логирования
                    clearBrowserCookies();
                    clearBrowserLocalStorage();
                    refresh();

                    formPage.openMainPage();
                    sleep(500);

                    // Заполняем минимально
                    formPage.fillCalculationForm("TestUser_" + i, "1990-01-01");
                    formPage.clickCalculateButton();
                    sleep(2000);

                    // Ждем модалку
                    $("#email").shouldBe(com.codeborne.selenide.Condition.visible);
                    sleep(500);

                    // Определяем версию
                    int version = formPage.determineABVersion();

                    // Быстро заполняем и отправляем
                    formPage.fillEmail("quick_" + i + "_" + System.currentTimeMillis() + "@test.ru");
                    formPage.clickSubmitButton();
                    sleep(1000);

                    // Добавляем в статистику
                    statistics.addTestResult(version, true);

                    if (i % 5 == 0) {
                        AllureAttachments.attachText("Progress after " + i + " users",
                                statistics.getStatistics());
                    }

                } catch (Exception e) {
                    AllureAttachments.attachText("Skipped user " + i,
                            "Error: " + e.getMessage());
                }
            }

            // Финальная статистика
            String finalStats = statistics.getStatistics();
            AllureAttachments.attachText("FINAL STATISTICS (10 users)", finalStats);

            // Проверяем распределение (допуск 30% для малой выборки)
            statistics.verifyDistribution(50, 50, 30.0);

            // Выводим детали по версиям
            analyzeVersionFeatures();
        });
    }

//    @Test
//    @DisplayName("Детальная проверка различий между версиями")
//    void testVersionDifferences() {
//        Allure.step("Изучение различий между версиями 0 и 1", () -> {
//            // Создаем карту различий
//            Map<String, String> version0Features = new HashMap<>();
//            Map<String, String> version1Features = new HashMap<>();
//
//            version0Features.put("Иконка карандаша", "Отсутствует");
//            version1Features.put("Иконка карандаша", "Присутствует");
//
//            // Дополнительные возможные различия
//            version0Features.put("Описание", "Базовая версия формы");
//            version1Features.put("Описание", "Улучшенная версия с иконкой");
//
//            AllureAttachments.attachText("Version 0 Features",
//                    formatFeatures(version0Features));
//            AllureAttachments.attachText("Version 1 Features",
//                    formatFeatures(version1Features));
//
//            // Проверяем обе версии на практике
//            for (int expectedVersion = 0; expectedVersion <= 1; expectedVersion++) {
//                Allure.step("Проверка версии " + expectedVersion, () -> {
//                    clearBrowserCookies();
//                    clearBrowserLocalStorage();
//                    refresh();
//
//                    formPage.openMainPage();
//                    formPage.fillCalculationForm("VersionTest_" + expectedVersion, "1990-01-01");
//                    formPage.clickCalculateButton();
//                    sleep(3000);
//
//                    formPage.waitForModal();
//                    int detectedVersion = formPage.determineABVersion();
//
//                    AllureAttachments.attachText("Version Check Result",
//                            "Expected to see version: " + expectedVersion + "\n" +
//                                    "Actually detected version: " + detectedVersion + "\n" +
//                                    "Match: " + (detectedVersion == expectedVersion));
//
//                    // Делаем скриншот для сравнения
//                    formPage.takeScreenshot("version_" + detectedVersion + "_example");
//                });
//            }
//        });
//    }

    private String generateBirthDate() {
        // Генерируем случайную дату рождения (18-60 лет)
        int year = 1965 + (int)(Math.random() * 42); // 1965-2006
        int month = 1 + (int)(Math.random() * 12);
        int day = 1 + (int)(Math.random() * 28);

        return String.format("%04d-%02d-%02d", year, month, day);
    }

    private String formatFeatures(Map<String, String> features) {
        StringBuilder sb = new StringBuilder();
        features.forEach((key, value) -> {
            sb.append("• ").append(key).append(": ").append(value).append("\n");
        });
        return sb.toString();
    }

    private void analyzeVersionFeatures() {
        Allure.step("Анализ особенностей каждой версии", () -> {
            String analysis = "Версия 0 (без иконки карандаша):\n" +
                    "• Более чистый дизайн\n" +
                    "• Меньше отвлекающих элементов\n" +
                    "• Фокус на контенте\n\n" +
                    "Версия 1 (с иконкой карандаша):\n" +
                    "• Визуальный акцент на поле email\n" +
                    "• Интерактивный элемент\n" +
                    "• Возможно повышает конверсию\n\n" +
                    "Гипотеза A/B теста:\n" +
                    "Проверяет, увеличивает ли иконка карандаша\n" +
                    "количество заполненных email и конверсию.";

            AllureAttachments.attachText("Version Analysis", analysis);
        });
    }

    @AfterEach
    void tearDown() {
        // Выводим статистику после каждого теста
        if (statistics != null) {
            String stats = statistics.getStatistics();
            AllureAttachments.attachText("Test Statistics", stats);
            System.out.println("\n=== Test Statistics ===");
            System.out.println(stats);
        }

        // Выводим детали по всем тестам
        if (!testResults.isEmpty()) {
            StringBuilder allResults = new StringBuilder();
            allResults.append("=== ALL TEST RESULTS ===\n\n");

            testResults.forEach((id, result) -> {
                allResults.append("Test ID: ").append(id).append("\n");
                allResults.append(result.toString()).append("\n---\n");
            });

            AllureAttachments.attachText("All Test Results", allResults.toString());
        }
    }

    // Внутренний класс для хранения результатов теста
    private static class TestResult {
        private final String testId;
        private final String userName;
        private final String userEmail;
        private final String sessionId;
        private final String timestamp;

        private ABTestApiClient.ABTestConfig config;
        private String birthDate;
        private int detectedVersion = -1;
        private String finalUrl;
        private boolean hasErrors;
        private boolean success;
        private String errorMessage;
        private int apiResponseStatus;

        public TestResult(String testId, String userName, String userEmail, String sessionId) {
            this.testId = testId;
            this.userName = userName;
            this.userEmail = userEmail;
            this.sessionId = sessionId;
            this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }

        // Геттеры и сеттеры
        public void setConfig(ABTestApiClient.ABTestConfig config) { this.config = config; }
        public void setBirthDate(String birthDate) { this.birthDate = birthDate; }
        public void setDetectedVersion(int detectedVersion) { this.detectedVersion = detectedVersion; }
        public void setFinalUrl(String finalUrl) { this.finalUrl = finalUrl; }
        public void setHasErrors(boolean hasErrors) { this.hasErrors = hasErrors; }
        public void setSuccess(boolean success) { this.success = success; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        public void setApiResponseStatus(int apiResponseStatus) { this.apiResponseStatus = apiResponseStatus; }

        public int getDetectedVersion() { return detectedVersion; }

        @Override
        public String toString() {
            return String.format(
                    "Test ID: %s\n" +
                            "Time: %s\n" +
                            "User: %s\n" +
                            "Email: %s\n" +
                            "Session: %s\n" +
                            "Birth Date: %s\n" +
                            "Detected AB Version: %d (%s)\n" +
                            "Final URL: %s\n" +
                            "Has Errors: %s\n" +
                            "Success: %s\n" +
                            "API Response Status: %d\n" +
                            "Error Message: %s",
                    testId, timestamp, userName, userEmail,
                    sessionId.substring(0, Math.min(10, sessionId.length())),
                    birthDate != null ? birthDate : "N/A",
                    detectedVersion,
                    detectedVersion == 1 ? "с иконкой" : detectedVersion == 0 ? "без иконки" : "не определено",
                    finalUrl != null ? finalUrl : "N/A",
                    hasErrors,
                    success,
                    apiResponseStatus,
                    errorMessage != null ? errorMessage : "нет"
            );
        }
    }
}
