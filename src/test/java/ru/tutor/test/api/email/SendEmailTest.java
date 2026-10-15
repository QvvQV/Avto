package ru.tutor.test.api.email;

import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.tutor.steps.catalog.SendEmailSteps;
import ru.tutor.config.TestConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ru.tutor.constants.Tags;

@Epic("Отправка писем")
@Story("Отправка письма")
@Tag(Tags.INTEGRATION)
public class SendEmailTest {
    private final SendEmailSteps sendEmailSteps = new SendEmailSteps(TestConfig.getCurrentApiUrl());

    @Test
    @DisplayName("Успешная отправка письма")
    @Tag(Tags.CONTRACT)
    void testSendEmail(){
        Response resp = sendEmailSteps.sendRandomEmail();
        assertEquals(200, resp.statusCode());
    }

    @Test
    @DisplayName("Отправка письма с длинным контентом")
    @Tag(Tags.CONTRACT)
    void testEmailWithLongContent(){
        Response resp = sendEmailSteps.sendEmailWithLongContent();
        assertEquals(200, resp.statusCode());
    }

    @Test
    @DisplayName("Отправка письма с невалидным email")
    @Tag(Tags.CONTRACT)
    void testInvalidEmail(){
        Response resp = sendEmailSteps.sendEmailWithInvalidEmail();
        assertEquals(400, resp.statusCode());
        Allure.addAttachment("Ошибка", "Нет валидации невалидных emails. Поэтому тест падает");
    }

    @Test
    @DisplayName("Отправка пустого письма")
    @Tag(Tags.CONTRACT)
    void testSendEmailEmpty(){
        sendEmailSteps.sendEmailWithEmptyBody();
    }

    @Test
    @DisplayName("Отправка письма с пустым сообщением")
    @Tag(Tags.CONTRACT)
    void testSendEmailEmptyContent(){
        sendEmailSteps.sendEmailWithEmptyContent();
    }

    @Test
    @DisplayName("Отправка письма с пустым email")
    @Tag(Tags.CONTRACT)
    void testSendEmailEmptyEmail(){
        sendEmailSteps.sendEmailWithEmptyEmail();
    }

    @Test
    @DisplayName("Отправка письма с пустым name")
    @Tag(Tags.CONTRACT)
    void testSendEmailEmptyName(){
        sendEmailSteps.sendEmailWithEmptyName();
    }
}
