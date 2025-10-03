package ru.tutor.api.Steps;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.page.Api.SendEmailApi;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SendEmailSteps {
    private final SendEmailApi sendEmailApi;

    public SendEmailSteps(String baseUrl) {
        this.sendEmailApi = new SendEmailApi(baseUrl);
    }

    @Step("Отправить письмо с name='{name}', email='{email}', content='{content}'")
    public Response sendEmail(String name, String email, String content) {
        return sendEmailApi.sendEmail(name, email, content);
    }

    @Step("Отправка случайного письма")
    public Response sendRandomEmail() {
        Response response = sendEmailApi.sendRandomEmail();
        assertEquals(200, response.statusCode(), "Ошибка при отправке случайного письма");
        return response;
    }

    @Step("Отправка письма с длинным контентом")
    public Response sendEmailWithLongContent() {
        Response response = sendEmailApi.sendRandomEmail();
        assertEquals(200, response.statusCode(), "Ошибка при отправке длинного письма");
        return response;
    }

    @Step("Отправка письма с невалидным email")
    public Response sendEmailWithInvalidEmail() {
        Response response = sendEmailApi.sendEmailWithInvalidEmail();
        assertEquals(400, response.statusCode());
        return response;
    }

    @Step("Отправка письма с пустым именем")
    public void sendEmailWithEmptyName() {
        Response response = sendEmailApi.sendEmailWithEmptyName();
        assertEquals(422, response.statusCode());
        String errorName = response.jsonPath().getString("error.messages.name");

        Allure.step("Проверка на значение ошибок в Body", () -> {
            assertEquals("Значение не должно быть пустым.", errorName, "Ошибка для name некорректная");
        });
    }

    @Step("Отправка письма с пустым email")
    public void sendEmailWithEmptyEmail() {
        Response response = sendEmailApi.sendEmailWithEmptyEmail();
        assertEquals(422, response.statusCode());
        String errorEmail = response.jsonPath().getString("error.messages.email");

        Allure.step("Проверка на значение ошибок в Email", () -> {
            assertEquals("Значение не должно быть пустым.", errorEmail, "Ошибка для email некорректная");
        });
    }

    @Step("Отправка письма с пустым сообщением")
    public void sendEmailWithEmptyContent() {
        Response response = sendEmailApi.sendEmailWithEmptyContent();
        assertEquals(422, response.statusCode());
        String errorContent = response.jsonPath().getString("error.messages.content");

        Allure.step("Проверка на значение ошибок в Сообщении", () -> {
            assertEquals("Значение не должно быть пустым.", errorContent, "Ошибка для content некорректная");
        });
    }

    @Step("Отправка письма с пустым Body")
    public void sendEmailWithEmptyBody() {
        Response response = sendEmailApi.sendEmailWithEmptyBody();
        assertEquals(422, response.statusCode());

        // Проверка тела ответа
        String errorEmail = response.jsonPath().getString("error.messages.email");
        String errorName = response.jsonPath().getString("error.messages.name");
        String errorContent = response.jsonPath().getString("error.messages.content");

        Allure.step("Проверка на значение ошибок в Body", () -> {
            assertEquals("Значение не должно быть пустым.", errorEmail, "Ошибка для email некорректная");
            assertEquals("Значение не должно быть пустым.", errorName, "Ошибка для name некорректная");
            assertEquals("Значение не должно быть пустым.", errorContent, "Ошибка для content некорректная");
        });
    }
}
