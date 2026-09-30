package ru.tutor.steps.promoCode;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.api.account.UserApi;
import ru.tutor.base.BaseApiSteps;
import ru.tutor.constants.PromoCodeTestData;
import ru.tutor.model.promoCode.ApplyPromoCodeRequest;
import ru.tutor.model.promoCode.ApplyPromoCodeResponse;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.utils.AllureAttachments;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PromoCodeSteps  extends BaseApiSteps {

    private final UserApi userApi;
    private final AuthSteps authSteps;

    public PromoCodeSteps(String baseUrl) {
        super(baseUrl);
        this.userApi = new UserApi(baseUrl);
        this.authSteps = new AuthSteps(baseUrl);
    }

    @Step("POST /user/promo-codes/apply - применение промокода")
    public Response applyPromoCode(String authToken, ApplyPromoCodeRequest request) {
        String effectiveToken = authToken;
        if (effectiveToken == null || effectiveToken.isEmpty()) {
                effectiveToken = authSteps.getTokenFromUserTab();
            Allure.addAttachment("Использование токена", "text/plain", "Получен новый токен из вкладки пользователя");
            }

        AllureAttachments.attachJson("Apply Promo Code Request",
                String.format("{\"promoCodeName\":\"%s\",\"orderNumber\":\"%s\"}",
                        request.promoCodeName(), request.orderNumber()));
        Response response = userApi.applyPromoCode(effectiveToken, request);
        AllureAttachments.attachJson("Apply Promo Code Response", response.getBody().asString());
        AllureAttachments.attachJson("Apply Promo Code Headers", response.getHeaders().toString());
        return response;
    }

    @Step("Валидация успешного ответа при применении промокода")
    public ApplyPromoCodeResponse validateSuccessfulPromoCodeResponse(Response response) {
        assertBasicResponse(response);
        assertJsonFieldExists(response, "message");

        ApplyPromoCodeResponse promoResponse = response.as(ApplyPromoCodeResponse.class);
        assertNotNull(promoResponse, "Ответ промокода не должен быть null");
        assertTrue(PromoCodeTestData.isSuccessResponse(promoResponse.getMessage()), "Ответ промокода должен быть успешным");

        AllureAttachments.attachText("Промокод применен",
                String.format("""
                                ✅ Промокод успешно применен
                                📝 Сообщение: %s
                                """,
                        promoResponse.getMessage()));

        return promoResponse;
    }

    @Step("Валидация ответа с ошибкой при применении промокода")
    public ApplyPromoCodeResponse validatePromoCodeErrorResponse(Response response, int expectedStatusCode, String expectedError) {
        int statusCode = response.getStatusCode();

        assertErrorStatusCode(response, expectedStatusCode);
        assertContentTypeJson(response);

        assertJsonFieldExists(response, "error");

        ApplyPromoCodeResponse promoResponse = response.as(ApplyPromoCodeResponse.class);
        assertNotNull(promoResponse, "Ответ промокода не должен быть null");
        assertTrue(promoResponse.hasError(), "Должно быть сообщение об ошибке");

        String actualErrorType = PromoCodeTestData.getErrorType(promoResponse.getError());

        if (expectedError != null && !expectedError.isEmpty()) {
            assertTrue(PromoCodeTestData.isExpectedError(promoResponse.getError(), expectedError),
                    String.format("Ошибка должна содержать '%s'. Фактически: '%s'",
                            expectedError, actualErrorType, promoResponse.getError()));
        }

        AllureAttachments.attachText("Ошибка применения промокода",
                String.format("""
                        ❌ Промокод не применен
                        📊 Статус: %d
                        📝 Ошибка: %s
                        """,
                        statusCode,
                        actualErrorType,
                        promoResponse.getError()));

        return promoResponse;
    }

    @Step("Применение промокода с проверкой успеха")
    public ApplyPromoCodeResponse applyPromoCodeAndValidate(String promoCodeName,
                                                            String orderNumber) {
        String authToken = authSteps.getTokenFromUserTab();
        ApplyPromoCodeRequest request = ApplyPromoCodeRequest.of(promoCodeName, orderNumber);
        Response response = applyPromoCode(authToken, request);
        return validateSuccessfulPromoCodeResponse(response);
    }

    @Step("Применение промокода с ожидаемой ошибкой")
    public ApplyPromoCodeResponse applyPromoCodeAndExpectError(String promoCodeName,
                                                               String orderNumber, String expectedError) {
        String authToken = authSteps.getTokenFromUserTab();

        ApplyPromoCodeRequest request = ApplyPromoCodeRequest.of(promoCodeName, orderNumber);
        Response response = applyPromoCode(authToken, request);
        return validatePromoCodeErrorResponse(response, 400, expectedError);
    }
}
