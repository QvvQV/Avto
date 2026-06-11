package ru.tutor.test.api;


import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.tutor.api.PaymentApi;
import ru.tutor.config.TestConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Задача 7221 "Добавить строгую валидацию email при создании заказа на бэке"
 */

@Epic("Email Validation")
@Feature("Order Creation Email Validation")
@DisplayName("Тесты валидации email при создании заказа")
public class EmailValidationApiTest {

    private final PaymentApi paymentApi = new PaymentApi(TestConfig.getCurrentApiUrl());
    private static final String PRODUCT_ID = "519";
    private static final String stEmail = "limbo";

@ParameterizedTest
    @ValueSource(strings = {
            "@test.ru",
            "_email@test.ru",
            "-email@test.ru",
            ".email@test.ru",
            "@email.test.ru",
            "@test-order.ru",
    })
    @DisplayName("POST /order с корректным email -> 200")
    void testValidEmailReturns200(String validEmail)
    {
        Response response = paymentApi.createOrderWithoutAuthMultipart(stEmail + validEmail, PRODUCT_ID);
        assertEquals(200, response.getStatusCode(),
                "Ожидался 200 для email: " + validEmail);
    }

    @Test
    @DisplayName("POST /order с email > 254 символов ->4xx")
    void testEmailTooLong()
    {
        String local = "p".repeat(119);
        String email = local + "@test.ru";
        Response response = paymentApi.createOrderWithoutAuthMultipart(email, PRODUCT_ID);
        assertTrue(response.getStatusCode() >= 400 && response.getStatusCode() < 500);
    }

    @ParameterizedTest
    @ValueSource(strings = {
//            ".testru",
            "@@test.ru",
            "@",
            "@test",
            "..email@test.ru",
            "test.@example.com",
            "@-test.ru",
            "@test-.ru",
            "@test.r.",
            "*@test.ru",
            "#@test.ru",
            "$@test.ru",
            "%@test.ru",
            " @test.ru",
            "+@test.ru",
            "!@test.ru",
            "/@test.ru",
            "^@test.ru",
            "`@test.ru",
            "?@test.ru",
            "=@test.ru",
//            "-@test.ru",
            "\\@test.ru",
            "|@test.ru",
            "()@test.ru",
            "[]@test.ru",
            "{}@test.ru",
    })
    @DisplayName("POST /order с некорректным email -> 4xx")
    void testInvalidEmailReturns4xx(String invalidEmail){
    Response response = paymentApi.createOrderWithoutAuthMultipart(stEmail + invalidEmail, PRODUCT_ID);
    assertTrue(response.getStatusCode() >= 400 && response.getStatusCode() < 500,
            "Ожидался 4хх для email: " + invalidEmail);
    }
}