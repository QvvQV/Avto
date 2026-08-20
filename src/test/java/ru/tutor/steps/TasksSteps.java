package ru.tutor.steps;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.api.UserApi;
import ru.tutor.base.BaseApiSteps;
import ru.tutor.utils.AllureAttachments;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TasksSteps extends BaseApiSteps {

    private final UserApi userApi;

    public TasksSteps(String baseUrl) {
        super(baseUrl);
        this.userApi = new UserApi(baseUrl);
    }

    @Step("GET /user/tasks - список заданий")
    public Response getTasks(String authToken) {
        return this.userApi.getUserTasks(authToken);
    }

    @Step("POST /user/tasks/change/{id} - заменить задание")
    public Response changeTask(String authToken, String id) {
        Response response = this.userApi.postUserTasksChange(authToken, id);
        AllureAttachments.attachJson("Tasks Response", response.getBody().asString());
        return response;
    }

    @Step("POST /user/tasks/finish/{id} - отметить задание завершенным")
    public Response finishTask(String authToken, String id) {
        Response response = this.userApi.postUserTasksFinish(authToken, id);
        AllureAttachments.attachJson("Tasks Response", response.getBody().asString());
        return response;
    }

    @Step("Извлечение id первого задания")
    public String extractFirstTaskId(Response response) {
        String taskId = response.jsonPath().getString("day[0].id");
        assertNotNull(taskId, "id задания не должны быть null");
        assertFalse(taskId.isEmpty(), "id задания не должны быть пустыми");
        Allure.addAttachment("Extracted taskId", taskId);
        return taskId;
    }

    @Step("Валидация ответа списка заданий")
    public void validateTasksResponse(Response response) {
        assertBasicResponse(response);
        String body = response.getBody().asString();
        assertNotNull(body, "Тело ответа не должно быть null");
        assertFalse(body.isEmpty(), "Тело ответа не должно быть пустым");
    }
}
