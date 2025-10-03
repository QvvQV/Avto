package ru.tutor.test.api;

import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.api.Steps.SendEmailSteps;
import ru.tutor.page.addressPage;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(AllureJunit5.class)
public class SendEmailTest {
    private final SendEmailSteps sendEmailSteps = new SendEmailSteps(addressPage.apiDevAdress);

    @Test
    @DisplayName("Успешная отправка письма")
    void testSendEmail(){
        Response resp = sendEmailSteps.sendRandomEmail();
        assertEquals(200, resp.statusCode());
    }

    @Test
    @DisplayName("Отправка пиьсма с длинным контентом")
    void testEmailWithLongContent(){
        Response resp = sendEmailSteps.sendEmailWithLongContent();
        assertEquals(200, resp.statusCode());
    }

    @Test
    @DisplayName("Отправка письма с невалидным email")
    void testInvalidEmail(){
        Response resp = sendEmailSteps.sendEmailWithInvalidEmail();
        assertEquals(400, resp.statusCode());
    }

    @Test
    @DisplayName("Отправка пустого письма")
    void testSendEmailEmpty(){
        sendEmailSteps.sendEmailWithEmptyBody();
    }

    @Test
    @DisplayName("Отправка письма с пустым сообщением")
    void testSendEmailEmptyContent(){
        sendEmailSteps.sendEmailWithEmptyContent();
    }

    @Test
    @DisplayName("Отправка письма с пустым email")
    void testSendEmailEmptyEmail(){
        sendEmailSteps.sendEmailWithEmptyEmail();
    }

    @Test
    @DisplayName("Отправка письма с пустым email")
    void testSendEmailEmptyName(){
        sendEmailSteps.sendEmailWithEmptyName();
    }
}
