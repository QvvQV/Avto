package ru.tutor.test.api.email;

import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
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
@Feature("Отправка письма")
@DisplayName("Тесты эндпоинта POST /send-email")
@Tag(Tags.INTEGRATION)
@Tag(Tags.API)
public class SendEmailTest {
    private final SendEmailSteps sendEmailSteps = new SendEmailSteps(TestConfig.getCurrentApiUrl());

    @Test
    @DisplayName("Успешная отправка письма")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.CONTRACT)
    void sendEmail(){
        Response resp = sendEmailSteps.sendRandomEmail();
        assertEquals(200, resp.statusCode());
    }

    @Test
    @DisplayName("Отправка письма с длинным контентом")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.POSITIVE)
    @Tag(Tags.CONTRACT)
    void sendEmailWithLongContent(){
        Response resp = sendEmailSteps.sendEmailWithLongContent();
        assertEquals(200, resp.statusCode());
    }

    @Test
    @DisplayName("Отправка письма с невалидным email")
    @Story("Валидация")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void sendEmailWithInvalidFormat(){
        Response resp = sendEmailSteps.sendEmailWithInvalidEmail();
        assertEquals(400, resp.statusCode());
        Allure.addAttachment("Известный баг", "POST /send-email не валидирует формат email: возвращает 200 вместо 400");
    }

    @Test
    @DisplayName("Отправка пустого письма")
    @Story("Валидация")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void sendEmailWithEmptyBody(){
        sendEmailSteps.sendEmailWithEmptyBody();
    }

    @Test
    @DisplayName("Отправка письма с пустым сообщением")
    @Story("Валидация")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void sendEmailWithEmptyContent(){
        sendEmailSteps.sendEmailWithEmptyContent();
    }

    @Test
    @DisplayName("Отправка письма с пустым email")
    @Story("Валидация")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void sendEmailWithEmptyEmail(){
        sendEmailSteps.sendEmailWithEmptyEmail();
    }

    @Test
    @DisplayName("Отправка письма с пустым name")
    @Story("Валидация")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void sendEmailWithEmptyName(){
        sendEmailSteps.sendEmailWithEmptyName();
    }
}
