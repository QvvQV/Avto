package ru.tutor.steps.fortune;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.api.account.UserApi;
import ru.tutor.base.BaseApiSteps;
import ru.tutor.utils.AllureAttachments;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

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

    @Step("Извлечение объекта задания {id}")
    public Map<String, Object> extractTaskMap(Response taskResponse, String taskId) {
        List<Map<String, Object>> tasks = taskResponse.jsonPath().getList("day");
        return tasks.stream()
                .filter(t -> taskId.equals(String.valueOf(t.get("id"))))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Задание " + taskId + " не найдено в day[]"));
    }

    @Step("Объект задания {id} изменился после")
    public void assertTaskObjectChanged(String authToken, String taskId, Map<String, Object> beforeTask) {
        Map<String, Object> afterTask = extractTaskMap(getTasks(authToken), taskId);
        assertNotEquals(beforeTask, afterTask, "Объект задания " + taskId + " должен измениться после");
    }

    @Step("Список id заданий в day[]")
    public Set<String> extractTaskIds(Response tasksResponse) {
        return tasksResponse.jsonPath().<Map<String, Object>>getList("day").stream()
                .map(t -> String.valueOf(t.get("id")))
                .collect(Collectors.toSet());
    }

    @Step("Задание заменено на новое")
    public void assertTaskReplaced(String authToken, String replacedTaskId, Set<String> idsBefore) {
        Set<String> idsAfter = extractTaskIds(getTasks(authToken));
        assertTrue(!idsAfter.contains(replacedTaskId), "Старое задание " + replacedTaskId + " должно исчезнуть из day[]");
        assertTrue(idsAfter.stream().anyMatch(id -> !idsBefore.contains(id)),
                "Вместо " + replacedTaskId + " должно появиться новое задание");
    }
}
