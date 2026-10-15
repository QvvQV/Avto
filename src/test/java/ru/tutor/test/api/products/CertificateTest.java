package ru.tutor.test.api.products;

import io.qameta.allure.*;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.junit.jupiter.api.*;
import ru.tutor.config.TestConfig;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.content.CertificateSteps;
import ru.tutor.steps.catalog.ProductsSteps;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import ru.tutor.constants.Tags;

@Epic("Сертификаты")
@Feature("Генерация сертификата")
@DisplayName("Тесты GET /user/certificate/generate")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

@Tag(Tags.INTEGRATION)
@Tag(Tags.API)
public class CertificateTest {

    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private final String TEST_EMAIL = TestConfig.getTestEmail();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();
    private final Faker faker = new Faker();

    private final AuthSteps userSteps = new AuthSteps(BASE_URL);
    private final ProductsSteps productsSteps = new ProductsSteps(BASE_URL);
    private final CertificateSteps certificateSteps = new CertificateSteps(BASE_URL);

    private String token;
    private String orderNumber;

    @BeforeEach
    void setUp() {
        token = step("Логин пользователя", () ->
                userSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );
    }

    private void ensureOrderNumber() {
        if (orderNumber != null) return;
        {
            orderNumber = productsSteps.fetchFirstOrderNumber(token);
        }
    }

    @Test
    @Order(1)
    @DisplayName("Успешная генерация сертификата")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.POSITIVE)
    void generateCertificateSuccess() {
        ensureOrderNumber();
        String name = faker.name().firstName();

        Response response = step("GET /user/certificate/generate", () ->
                certificateSteps.generateCertificate(token, name, orderNumber)
        );

        step("Валидация ответа", () ->
                certificateSteps.validateCertificateGenerateResponse(response)
        );

        String pdfPath = response.jsonPath().getString("pdf");
        Response pdf = step("GET " + pdfPath, () ->
                certificateSteps.getCertificateFile(token, pdfPath));
        step("Проверка файла PDF сертификата", () ->
                certificateSteps.assertCertificateFileAvailable(pdf));
    }

    @Test
    @Order(2)
    @DisplayName("Генерация сертификата без авторизации")
    @Severity(SeverityLevel.CRITICAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void generateCertificateWithoutAuth() {
        ensureOrderNumber();
        String name = faker.name().firstName();

        Response response = step("GET /user/certificate/generate без токена", () ->
                certificateSteps.generateCertificate("", name, orderNumber)
        );

        step("Проверка статуса 401", () ->
                certificateSteps.assertUnauthorizedWithMessage(response, "JWT Token not found"));
    }

    @Test
    @Order(3)
    @DisplayName("Генерация сертификата с невалидным токеном")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void generateCertificateInvalidToken() {
        ensureOrderNumber();
        String name = faker.name().firstName();

        Response response = step("GET /user/certificate/generate с invalid_token", () ->
                certificateSteps.generateCertificate("invalid_token", name, orderNumber)
        );

        step("Проверка статуса 401", () ->
                certificateSteps.assertUnauthorizedWithMessage(response, "Invalid JWT Token"));
    }

    @Test
    @Order(4)
    @DisplayName("Генерация сертификата с invalid_order")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void generateCertificateInvalidOrder() {
        ensureOrderNumber();
        String name = faker.name().firstName();

        Response response = step("GET /user/certificate/generate с невалидным номером заказа", () ->
                certificateSteps.generateCertificate(token, name, "invalid_order")
        );

        step("Проверка статуса 404 и сообщения об ошибке", () -> {
                    assertEquals(404, response.getStatusCode());
                    String message = response.jsonPath().getString("message");
                    assertEquals("Order not found", message);
                }
        );
    }

    @Test
    @Order(5)
    @DisplayName("Генерация сертификата пустое значение заказа")
    @Severity(SeverityLevel.NORMAL)
    @Tag(Tags.NEGATIVE)
    @Tag(Tags.CONTRACT)
    void generateCertificateWithouthOrder() {
        ensureOrderNumber();
        String name = faker.name().firstName();

        Response response = step("GET /user/certificate/generate с пустым номером заказа", () ->
                certificateSteps.generateCertificate(token, name,"")
        );

        step("Проверка статуса 404 и сообщения об ошибке", () -> {
                    assertEquals(404, response.getStatusCode());
                    String message = response.jsonPath().getString("message");
                    assertEquals("Order not found", message);
                }
        );
    }
}