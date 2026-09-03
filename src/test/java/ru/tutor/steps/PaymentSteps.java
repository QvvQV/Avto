package ru.tutor.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.base.BaseApiSteps;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentSteps extends BaseApiSteps {

    public PaymentSteps(String baseUrl) {
        super(baseUrl);
    }

    @Step("Проверка списка платёжных систем ({jsonPath})")
    public void assertPaymentSystems(Response response, String jsonPath) {
        assertBasicResponse(response);
        List<String> names = response.jsonPath().getList(jsonPath);
        assertNotNull(names, "Список платёжных систем не должен быть null");
        assertTrue(names.contains("CloudPayments"), "Ожидалось CloudPayments, получено: " + names);
        assertTrue(names.contains("sberbank"), "Ожидалось sberbank, получено: " + names);
        assertTrue(names.contains("Others"), "Ожидалось Others, получено: " + names);
        assertTrue(names.contains("sberpay"), "Ожидалось sberpay, получено: " + names);
    }

    @Step("Проверка версии модалки и сплита")
    public void assertModalVersion(Response response) {
        int splitAlter = response.jsonPath().getInt("splitAlter");
        assertAll(
                () -> assertTrue(response.jsonPath().getInt("showModalVersion") >= 1,
                        "showModalVersion должен быть >= 1"),
                () -> assertTrue(splitAlter == 0 || splitAlter == 1,
                        "splitAlter должен быть 0")
        );
    }
}
