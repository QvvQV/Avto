package ru.tutor.steps;








//
//import io.qameta.allure.Allure;
//import io.qameta.allure.Step;
//import io.restassured.http.ContentType;
//import io.restassured.response.Response;
//import net.datafaker.Faker;
//import ru.tutor.api.PaymentApi;
//import ru.tutor.constants.PaymentConstants;
//import ru.tutor.model.fortune.UserRewardResponse;
//import ru.tutor.model.order.PaymentParams;
//import ru.tutor.model.order.PaymentRequest;
//import ru.tutor.model.order.PaymentResponse;
//import ru.tutor.utils.AllureAttachments;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class PaymentSteps {
//    private final PaymentApi paymentApi;
//    private final Faker faker;
//
//    public PaymentSteps(String baseUrl) {
//        this.paymentApi = new PaymentApi(baseUrl);
//        this.faker = new Faker();
//    }
//
//    @Step("Создать платежный запрос с уникальным email")
//    public PaymentRequest createPaymentRequestWithUniqueEmail() {
//        PaymentRequest request = createMinimalPaymentRequest();
//        request.refreshEmail();
//        return request;
//    }
//
//    @Step("Создать платежный запрос с кастомным email: {email}")
//    public PaymentRequest createMinimalPaymentRequest (String email) {
//        PaymentRequest request = createMinimalPaymentRequest();
//        request.setEmail(email);
//        return request;
//    }
//
//    @Step("Создать платеж и получить ответ")
//    public Response createOrder(PaymentRequest paymentRequest) {
//        return paymentApi.createOrderWithoutAuth(paymentRequest);
//    }
//
//    @Step("Проверить успешное создание платежа")
//    public PaymentResponse createOrderSuccessfully(PaymentRequest paymentRequest) {
//        Response response = createOrder(paymentRequest);
//
//        response.then()
//                .statusCode(200)
//                .contentType(ContentType.JSON);
//
//        // Преобразуем в список модели
//        PaymentResponse userRewards = response.as(PaymentResponse.class);
//
//        return userRewards;
//    }
//
//    @Step("Создать платеж и проверить валидацию")
//    public Response createOrderAndvalidate(PaymentRequest paymentRequest) {
//        validatePaymentRequest(paymentRequest);
//        return createOrder(paymentRequest);
//    }
//
//    @Step("Валидировать платежный запрос")
//    public void validatePaymentRequest(PaymentRequest paymentRequest) {
//        if (!paymentRequest.hasAccountId()) {
//            throw new IllegalArgumentException("AccountId обязателен");
//        }
//        if (!paymentRequest.hasValidAmount()) {
//            throw new IllegalArgumentException("Amount должен быть положительным числом");
//        }
//        if (!paymentRequest.hasDescription()) {
//            throw new IllegalArgumentException("Description обязателен");
//        }
//        if (!paymentRequest.hasEmail()) {
//            throw new IllegalArgumentException("Email обязателен");
//        }
//        if (!paymentRequest.isValidEmailFormat()) {
//            throw new IllegalArgumentException("Email имеет неверный формат: " + paymentRequest.getEmail());
//        }
//    }
//
//    @Step("Проверить, что платеж создан успешно")
//    public void verifyOrderCreatedSuccessfully(Response response) {
//        response.then()
//                .statusCode(200)
//                .contentType(ContentType.JSON);
//    }
//
//    @Step("Проверяем, что mail в ответе соответсвует запросу")
//    public void verifyEmailInResponse(Response response, PaymentRequest request) {
//        String responseBody = response.getBody().asString();
//        String expectedEmail = request.getEmail();
//
//        if (!responseBody.contains(expectedEmail)) {
//            if (responseBody.contains("email") || responseBody.contains("Email")) {
//                System.out.println(responseBody);
//            } else {
//                throw new AssertionError("Email не найден в ответе");
//            }
//        }
//    }
//}
