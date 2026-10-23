package ru.tutor.test.api.user;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import ru.tutor.config.TestConfig;
import ru.tutor.flow.account.FreshUserFixture;
import ru.tutor.steps.fortune.TasksSteps;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import ru.tutor.constants.Tags;

import java.util.Map;
import java.util.Set;

@Epic("API Тесты пользователя")
@Feature("Система заданий")
@DisplayName("Тесты эндпоинтов заданий")
@Tag(Tags.INTEGRATION)
@Tag(Tags.API)
public class UserTasksTest {

    private final TasksSteps userSteps = new TasksSteps(TestConfig.getCurrentApiUrl());

    private static String token;

    @BeforeAll
    public static void setUp() {
        Allure.label("component", "API");
        Allure.label("feature", "Tasks");
    }

    @BeforeEach
    void setUpAuth() {
        token = step("Создание нового пользователя", () -> new FreshUserFixture().register().token());
    }

    @Test
    @DisplayName("GET /user/tasks - успешное получение списка заданий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.CONTRACT)
    void getTasksSuccess() {
        Response response = step("GET /user/tasks", () ->
                userSteps.getTasks(token)
        );
        step("Валидация ответа", () ->
                userSteps.validateTasksResponse(response)
        );
    }

    @Test
    @DisplayName("GET /user/tasks - без авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void getTasksWithoutAuth() {
        Response response = step("GET /user/tasks без токена", () ->
                userSteps.getTasks("")
        );
        step("Проверка статуса 401", () ->
                userSteps.assertUnauthorizedWithMessage(response, "JWT Token not found"));
    }

    @Test
    @DisplayName("GET /user/tasks - инвалид токен")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void getTasksInvalidToken() {
        Response response = step("GET /user/tasks инвалид токен", () ->
                userSteps.getTasks("invalid")
        );
        step("Проверка статуса 401", () ->
                userSteps.assertUnauthorizedWithMessage(response, "Invalid JWT Token"));
    }

    @Test
    @DisplayName("POST /user/tasks/change/{id} - замена задания")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    void changeTasksSuccess() {
        Response tasksResponse = step("GET /user/tasks", () ->
                userSteps.getTasks(token)
        );
        userSteps.validateTasksResponse(tasksResponse);

        String taskId = step("Извлечение id задания", () ->
                userSteps.extractFirstTaskId(tasksResponse)
        );
        Set<String> idsBefore = step("id заданий до замены", () ->
                userSteps.extractTaskIds(tasksResponse));
        Response changeResponse = step("POST /user/tasks/change/" + taskId, () ->
                userSteps.changeTask(token, taskId)
        );
        step("Проверка статуса 200", () ->
            assertEquals(200, changeResponse.getStatusCode()));
        step("Задание заменено (старый id исчез, появился новый)", () ->
                userSteps.assertTaskReplaced(token, taskId, idsBefore));
    }

    @Test
    @DisplayName("POST /user/tasks/change/{id} - без авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
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
                userSteps.assertUnauthorizedWithMessage(changeResponse, "JWT Token not found"));
    }

    @Test
    @DisplayName("POST /user/tasks/change/{id} - инвалид токен")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
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
                userSteps.assertUnauthorizedWithMessage(changeResponse, "Invalid JWT Token"));
    }

    @Test
    @DisplayName("POST /user/tasks/change/{id} - невалидный номер")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
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
    @DisplayName("POST /user/tasks/finish/{id} - завершение задания")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    void finishTasksSuccess() {
        Response tasksResponse = step("GET /user/tasks", () ->
                userSteps.getTasks(token)
        );
        userSteps.validateTasksResponse(tasksResponse);

        String taskId = step("Извлечение id задания", () ->
                userSteps.extractFirstTaskId(tasksResponse)
        );
        Map<String, Object> beforeTask = step("Объект задания до finish", () ->
                userSteps.extractTaskMap(tasksResponse, taskId));
        Response changeResponse = step("POST /user/tasks/finish/" + taskId, () ->
                userSteps.finishTask(token, taskId)
        );
        step("Проверка статуса 200", () ->
                assertEquals(200, changeResponse.getStatusCode())
        );

        step("Задание изменилось после finish", () ->
                userSteps.assertTaskObjectChanged(token, taskId, beforeTask));
    }

    @Test
    @DisplayName("POST /user/tasks/finish/{id} - без авторизации")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void finishTaskWithoutAuth() {
        Response tasksResponse = step("GET /user/tasks", () ->
                userSteps.getTasks(token)
        );
        userSteps.validateTasksResponse(tasksResponse);

        String taskId = step("Извлечение id задания", () ->
                userSteps.extractFirstTaskId(tasksResponse)
        );
        Response changeResponse = step("POST /user/tasks/change/ Пустой токен" + taskId, () ->
                userSteps.finishTask("", taskId)
        );
        step("Проверка статуса 401", () ->
                userSteps.assertUnauthorizedWithMessage(changeResponse, "JWT Token not found"));
    }

    @Test
    @DisplayName("POST /user/tasks/finish/{id} - пустой номер")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void finishTaskEmptyId() {
        Response tasksResponse = step("GET /user/tasks", () ->
                userSteps.getTasks(token)
        );
        userSteps.validateTasksResponse(tasksResponse);

        String taskId = step("Извлечение id задания", () ->
                userSteps.extractFirstTaskId(tasksResponse)
        );
        Response changeResponse = step("POST /user/tasks/change/ Пустой {id}" + taskId, () ->
                userSteps.finishTask(token, "")
        );
        step("Проверка статуса 404", () ->
                assertEquals(404, changeResponse.getStatusCode()));
    }
}