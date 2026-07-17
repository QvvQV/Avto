package ru.tutor.test.api.user;

import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.config.TestConfig;
import ru.tutor.steps.UserSteps;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(AllureJunit5.class)
@Epic("API Тесты пользователя")
@Feature("Система заданий")
@DisplayName("Тесты эндпоинтов заданий")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserTasksTest {

    private final UserSteps userSteps = new UserSteps(TestConfig.getCurrentApiUrl());
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    private static String token;

    @BeforeAll
    public static void setUp() {
        Allure.label("component", "API");
        Allure.label("feature", "Tasks");
    }

    @BeforeEach
    void setUpAuth() {
        // Логин перед каждым тестом
        token = step("Логин пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );
    }

    @Test
    @Order(1)
    @DisplayName("GET /user/tasks - успешное получение списка заданий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("positive")
    void getTasksSuccess() {
        Response response = step("GET /user/tasks", () ->
                userSteps.getTasks(token)
        );
        step("Валидация ответа", () ->
                userSteps.validateTasksResponse(response)
        );
    }

    @Test
    @Order(2)
    @DisplayName("GET /user/tasks - без авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void getTasksWithoutAuth() {
        Response response = step("GET /user/tasks без токена", () ->
                userSteps.getTasks("")
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(3)
    @DisplayName("GET /user/tasks - инвалид токен")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void getTasksInvalidToken() {
        Response response = step("GET /user/tasks инвалид токен", () ->
                userSteps.getTasks("invalid")
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, response.getStatusCode()));
    }

    @Test
    @Order(4)
    @DisplayName("POST /user/tasks/change/{id} - замена задания")
    @Severity(SeverityLevel.NORMAL)
    @Tag("positive")
    void changeTasksSuccess() {
        Response tasksResponse = step("GET /user/tasks", () ->
                userSteps.getTasks(token)
        );
        userSteps.validateTasksResponse(tasksResponse);

        String taskId = step("Извлечение id задания", () ->
                userSteps.extractFirstTaskId(tasksResponse)
        );
        Response changeResponse = step("POST /user/tasks/change/" + taskId, () ->
                userSteps.changeTask(token, taskId)
        );
        step("Проверка статуса", () -> {
            int statusCode = changeResponse.getStatusCode();
            if (statusCode == 200) {
                assertEquals(200, changeResponse.getStatusCode());
            } else if (statusCode == 500) {
                String detail = changeResponse.jsonPath().getString("detail");
                assertEquals("USER_HAS_ALREADY_CHANGED_THE_TASK", detail,
                        "Ожидаемая бизнес-ошибка, а не неизвестная");
            } else {
                fail("Неожиданный статус: " +  statusCode);
            }
        });
    }

    @Test
    @Order(5)
    @DisplayName("POST /user/tasks/change/{id} - без авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void changeTaskWithoutAuth() {
        Response tasksResponse = step("GET /user/tasks", () ->
                userSteps.getTasks(token)
        );
        userSteps.validateTasksResponse(tasksResponse);

        String taskId = step("Извлечение id задания", () ->
                userSteps.extractFirstTaskId(tasksResponse)
        );
        Response changeResponse = step("POST /user/tasks/change/" + taskId, () ->
                userSteps.changeTask("", taskId)
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, changeResponse.getStatusCode()));
    }

    @Test
    @Order(6)
    @DisplayName("POST /user/tasks/change/{id} - инвалид токен")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void changeTaskInvalidToken() {
        Response tasksResponse = step("GET /user/tasks", () ->
                userSteps.getTasks(token)
        );
        userSteps.validateTasksResponse(tasksResponse);

        String taskId = step("Извлечение id задания", () ->
                userSteps.extractFirstTaskId(tasksResponse)
        );
        Response changeResponse = step("POST /user/tasks/change/" + taskId, () ->
                userSteps.changeTask("invalid_token", taskId)
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, changeResponse.getStatusCode()));
    }

    @Test
    @Order(7)
    @DisplayName("POST /user/tasks/change/{id} - невалидный номер")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void changeTaskEmpty() {
        Response tasksResponse = step("GET /user/tasks", () ->
                userSteps.getTasks(token)
        );
        userSteps.validateTasksResponse(tasksResponse);

        String taskId = step("Извлечение id задания", () ->
                userSteps.extractFirstTaskId(tasksResponse)
        );
        Response changeResponse = step("POST /user/tasks/change/" + taskId, () ->
                userSteps.changeTask(token, "")
        );
        step("Проверка статуса 404", () ->
                assertEquals(404, changeResponse.getStatusCode()));
    }

    @Test
    @Order(8)
    @DisplayName("POST /user/tasks/finish/{id} - завершение задания")
    @Severity(SeverityLevel.NORMAL)
    @Tag("positive")
    void finishTasksSuccess() {
        Response tasksResponse = step("GET /user/tasks", () ->
                userSteps.getTasks(token)
        );
        userSteps.validateTasksResponse(tasksResponse);

        String taskId = step("Извлечение id задания", () ->
                userSteps.extractFirstTaskId(tasksResponse)
        );
        Response changeResponse = step("POST /user/tasks/finish/" + taskId, () ->
                userSteps.finishTask(token, taskId)
        );
        step("Проверка статуса 200", () ->
                assertEquals(200, changeResponse.getStatusCode())
        );
    }

    @Test
    @Order(9)
    @DisplayName("POST /user/tasks/finish/{id} - без авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void finishTaskWithoutAuth() {
        Response tasksResponse = step("GET /user/tasks", () ->
                userSteps.getTasks(token)
        );
        userSteps.validateTasksResponse(tasksResponse);

        String taskId = step("Извлечение id задания", () ->
                userSteps.extractFirstTaskId(tasksResponse)
        );
        Response changeResponse = step("POST /user/tasks/change/" + taskId, () ->
                userSteps.finishTask("", taskId)
        );
        step("Проверка статуса 401", () ->
                assertEquals(401, changeResponse.getStatusCode()));
    }

    @Test
    @Order(10)
    @DisplayName("POST /user/tasks/finish/{id} - пустой номер")
    @Severity(SeverityLevel.NORMAL)
    @Tag("negative")
    void finishTaskEmptyId() {
        Response tasksResponse = step("GET /user/tasks", () ->
                userSteps.getTasks(token)
        );
        userSteps.validateTasksResponse(tasksResponse);

        String taskId = step("Извлечение id задания", () ->
                userSteps.extractFirstTaskId(tasksResponse)
        );
        Response changeResponse = step("POST /user/tasks/change/" + taskId, () ->
                userSteps.finishTask(token, "")
        );
        step("Проверка статуса 404", () ->
                assertEquals(404, changeResponse.getStatusCode()));
    }
}