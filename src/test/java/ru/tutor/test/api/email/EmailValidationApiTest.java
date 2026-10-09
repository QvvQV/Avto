package ru.tutor.test.api.email;


import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.provider.ValueSource;
import ru.tutor.api.orders.PaymentApi;
import ru.tutor.config.TestConfig;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ru.tutor.constants.Tags;

/**
 * Задача 7221 "Добавить строгую валидацию email при создании заказа на бэке"
 */

@Epic("Email Validation")
@Feature("Order Creation Email Validation")
@DisplayName("Тесты валидации email при создании заказа")
@Tag(Tags.INTEGRATION)
public class EmailValidationApiTest {

    private final PaymentApi paymentApi = new PaymentApi(TestConfig.getCurrentApiUrl());
    private final Faker faker = new Faker(Locale.ENGLISH);
    private static final String PRODUCT_ID = "519";
    String stEmail = faker.name().firstName();

//@ParameterizedTest
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

//    @Test
    @DisplayName("POST /order с email > 128 символов ->4xx")
    void testEmailTooLong()
    {
        String local = "p".repeat(129);
        String email = local + "@test.ru";
        Response response = paymentApi.createOrderWithoutAuthMultipart(email, PRODUCT_ID);
        assertTrue(response.getStatusCode() >= 400 && response.getStatusCode() < 500);
    }

//    @ParameterizedTest
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