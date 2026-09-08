package ru.tutor.flow;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.api.UserProductsApi;
import ru.tutor.config.TestConfig;
import ru.tutor.constants.AddressPage;
import ru.tutor.model.ProductsResponse;
import ru.tutor.page.Registration;
import ru.tutor.page.admin.AdminOrdersPage;
import ru.tutor.page.modals.GreetingsModal;
import ru.tutor.page.modals.Product;
import ru.tutor.page.modals.UnlockNextWeek;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.orders.SberPage;
import ru.tutor.page.orders.TtPage;
import ru.tutor.steps.AdminToUserLoginSteps;
import ru.tutor.steps.AuthSteps;
import ru.tutor.steps.ProductsSteps;
import ru.tutor.utils.*;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RenewalModalFlow {
    private final UserProductsApi userProductsApi;
    private final ProductsSteps productsSteps;
    private final Registration registration;
    private final Product product;
    private final UnlockNextWeek unlockNextWeek;
    private final CloudPage cloudPage;
    private final AuthSteps authSteps;
    private final PurchaseFlow purchaseFlow;
    private final GreetingsModal greetingsModal;
    private String currentToken;
    private String userEmail;

    public RenewalModalFlow(
            Registration registration, Product product, UnlockNextWeek unlockNextWeek, CloudPage cloudPage,
            TtPage ttPage, SberPage sberPage
    ) {
        this.userProductsApi = new UserProductsApi(TestConfig.getCurrentApiUrl());
        this.productsSteps = new ProductsSteps(TestConfig.getCurrentApiUrl());
        this.registration = registration;
        this.product = product;
        this.unlockNextWeek = unlockNextWeek;
        this.cloudPage = cloudPage;
        this.authSteps = new AuthSteps(TestConfig.getCurrentApiUrl());
        this.purchaseFlow = new PurchaseFlow(ttPage, cloudPage, sberPage);
        this.greetingsModal = new GreetingsModal();
    }

    @Step("Логин Реневалла в ЛК prod")
    public RenewalModalFlow login() {
        if (TestConfig.isProd()) {
            currentToken = step("POST /user/login (renewal)", () ->
                    authSteps.loginAndGetToken(
                            TestConfig.getRenewalEmailProd(),
                            TestConfig.getTestPassword()));
            step("Открытие домена ЛК и подстановка токена", () -> {
                open(AddressPage.LOGIN);
                registration.loginWithRenewalProd();
            });
            WaitHelper.waitForPageLoad();
        } else {
            step("Создание заказа Лайт", () -> {
                PurchaseResult result = purchaseFlow.lite();
                userEmail = result.email();
            });
            step("Получение href со страницы успеха", () -> {
                cloudPage.verifySuccessProfilePage();
            });

            step("Переход в ЛК", () -> {
                FrameHelper.switchToDefaultContent();
                $$("a").find(exactText("Перейти в личный кабинет"))
                        .shouldBe(visible, Duration.ofSeconds(15))
                        .click();
                WaitHelper.waitForPageLoad();
            });
            step("Заполнение приветсвенной модалки", () ->
                    greetingsModal.fillAccessInput());
            step("Получение токена из localStorage", () ->
                    currentToken = AdminToUserLoginSteps.extractTokenFromCurrentPage());
        }
        return this;
    }

    @Step("Логин Реневалла (API) и получение токена")
    public RenewalModalFlow loginViaApi() {
        currentToken = step("POST /user/login (renewal)", () ->
                authSteps.loginAndGetToken(
                        TestConfig.getRenewalEmailProd(),
                        TestConfig.getTestPassword()));
        return this;
    }

    @Step("Первый продукт (не онбординг, не Лайт New) через /user/products -> открытие урока")
    public RenewalModalFlow openFirstProductLesson() {
        Response resp = step("GET /user/products", () ->
                userProductsApi.getUserProducts(currentToken));
        List<ProductsResponse> products = productsSteps.validateProductsResponse(resp);
        ProductsResponse first = products.stream()
                .filter(p -> !p.isOnboardingProduct())
                .filter(p -> !Objects.equals(p.getProductId(), "180"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Нет подходящего продукта (не onboarding, не Лайт New)"));

        String url = TestConfig.getCurrentClientDomain()
                + "/user/product/" + first.getProductId() + "/" + first.getId() + "/lesson/1";
        AllureAttachments.attachText("Открываем урок", url);
        step("Открытие урока: " + url, () -> open(url));
        return this;
    }

    @Step("До модалки оплаты (Неделя 1 урок 1 + вторая неделя)")
    public RenewalModalFlow processToPaymentModal() {
        product.beginProductProd();
        new UnlockNextWeek().openUnlimitedAccess();
        return this;
    }

    @Step("Фиксация появления payment-iframe (без оплаты)")
    public void assertPaymentFrameVisible() {
        boolean visible = cloudPage.isCloudPaymentFrameVisible();
        assertTrue(visible, "Payment-iframe не появился после перехода в модалку оплаты");
        AllureAttachments.attachScreenshot("Payment-iframe (Renewal, prod)");
    }

    @Step("Настройки платежей → 99 руб → /user/main → Cloud iframe")
    public RenewalModalFlow openPayments99AndAssertIframe() {
        step("Открытие /user/settings/payments", () -> open(AddressPage.PAYMENTS_SETTINGS));
        WaitHelper.waitForPageLoad();
        step("Клик 'Оформить доступ за 99 рублей'", () -> {
            $$("button").find(exactText("Оформить доступ за 99 рублей*"))
                    .shouldBe(visible, Duration.ofSeconds(15))
                    .click();
        });
        step("Ожидание редиректа на /user/main", () ->
                WaitHelper.waitForUrlContains("/user/main", Duration.ofSeconds(20)));
        if (TestConfig.isProd()) {
            step("Фиксация Cloud iframe", () -> {
                WaitHelper.waitFor(() -> cloudPage.isCloudPaymentFrameVisible(),
                        Duration.ofSeconds(30), "Cloud iframe не появился");
                assertTrue(cloudPage.isCloudPaymentFrameVisible(), "Cloud iframe не появился");
                AllureAttachments.attachScreenshot("Cloud iframe (99 руб, /user/main)");
            });

        } else {
            step("Продление оплаты (dev)", () ->
                    cloudPage.clickCardPageBtn());
            AllureAttachments.attachScreenshot("Оплата 99 руб (dev, /user/main)");
        }
        return this;
    }

    @Step("Проверка наличия продукта {productId} в /user/products")
    public RenewalModalFlow verifyProductPresent(int expectedProductId) {
        step("Получение токена из localStorage", () ->
                currentToken = AdminToUserLoginSteps.extractTokenFromCurrentPage());
        step("GET /user/products -> проверка productId " + expectedProductId, () -> {
            Response resp = userProductsApi.getUserProducts(currentToken);
            List<ProductsResponse> products = productsSteps.validateProductsResponse(resp);
            boolean found = products.stream()
                    .anyMatch(p -> Objects.equals(p.getProductId(), String.valueOf(expectedProductId)));
            assertTrue(found, "Продукт с productId=" + expectedProductId + " должен присутствовать");
            AllureAttachments.attachText("Products",
                    "ProductId " + expectedProductId + ": " + (found ? "найден" : "НЕ НАЙДЕН"));
        });
        return this;
    }

    @Step("Выход из сессии пользователя через очистку кук")
    public void logoutAfterTest() {
        FrameHelper.switchToDefaultContent();
        clearBrowserCookies();
        clearBrowserLocalStorage();
        refresh();
        WaitHelper.waitForPageLoad();
    }
}
