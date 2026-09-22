package ru.tutor.flow;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import net.datafaker.Faker;
import ru.tutor.api.UserProductsApi;
import ru.tutor.config.TestConfig;
import ru.tutor.constants.AddressPage;
import ru.tutor.model.LoginResponse;
import ru.tutor.model.ProductsResponse;
import ru.tutor.model.UserEditDataRequest;
import ru.tutor.page.Registration;
import ru.tutor.page.admin.AdminOrdersPage;
import ru.tutor.page.modals.GreetingsModal;
import ru.tutor.page.modals.Product;
import ru.tutor.page.modals.UnlockNextWeek;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.orders.SberPage;
import ru.tutor.page.orders.TtPage;
import ru.tutor.steps.*;
import ru.tutor.utils.*;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.codeborne.selenide.Condition.*;
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
    private final AccountSteps accountSteps;
    private final PurchaseFlow purchaseFlow;
    private final GreetingsModal greetingsModal;
    private String currentToken;
    private String userEmail;
    private final RevshareFlow revshareFlow;
    private final OrderToggleSubscriptionSteps orderToggleSubscriptionSteps;

    private Faker faker = new Faker(Locale.ENGLISH);

    public RenewalModalFlow(
            Registration registration, Product product, UnlockNextWeek unlockNextWeek, CloudPage cloudPage,
            TtPage ttPage, SberPage sberPage, RevshareFlow revshareFlow, OrderToggleSubscriptionSteps orderToggleSubscriptionSteps
    ) {
        this.userProductsApi = new UserProductsApi(TestConfig.getCurrentApiUrl());
        this.productsSteps = new ProductsSteps(TestConfig.getCurrentApiUrl());
        this.registration = registration;
        this.product = product;
        this.unlockNextWeek = unlockNextWeek;
        this.cloudPage = cloudPage;
        this.authSteps = new AuthSteps(TestConfig.getCurrentApiUrl());
        this.accountSteps = new AccountSteps(TestConfig.getCurrentApiUrl());
        this.purchaseFlow = new PurchaseFlow(ttPage, cloudPage, sberPage);
        this.greetingsModal = new GreetingsModal();
        this.revshareFlow = revshareFlow;
        this.orderToggleSubscriptionSteps = orderToggleSubscriptionSteps;
    }

    @Step("Логин Реневалла в ЛК prod")
    public RenewalModalFlow login() {
        if(TestConfig.isProd()) {
            step("Создание свежего заказа по тестовому домену", () ->
                    revshareFlow.executeProd());
            userEmail = cloudPage.getCurrentEmail();
        } else {
            step("Создание заказа Лайт", () -> {
                PurchaseResult result = purchaseFlow.lite();
                userEmail = result.email();
            });
        }

        step("Переход в ЛК", () -> {
            String href = cloudPage.verifySuccessProfilePage();
            String password = OrderSteps.extractQueryParam(href, "password");
            currentToken = authSteps.loginViaRefreshToken(userEmail, password);
            step("Заполнение приветственной модалки через API", () -> {
                accountSteps.validateEditPasswordSuccess(
                        accountSteps.editPassword(currentToken, password, TestConfig.getTestPassword())
                );
                accountSteps.validateEditDataSuccess(
                        accountSteps.editUserData(currentToken,
                                new UserEditDataRequest().name(faker.name().firstName()).gender(0).email(userEmail)
                ));
            });
            refresh();
        });

        if (TestConfig.isProd()) {
            step("Отмена заказа до Лайта (POST /user/orders/toggle_subscription/180)", () ->
                    orderToggleSubscriptionSteps.toggleOrderSubscription(currentToken, 180));
        }
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
        step("Проверка появления iframe", () -> {
            boolean visible = cloudPage.isCloudPaymentFrameVisible();
            assertTrue(visible, "Payment-iframe не появился после перехода в модалку оплаты");
        });
        AllureAttachments.attachScreenshot("Появление Payment-iframe (Renewal, prod)");
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

    private static final SelenideElement FIRST_NON_ONBOARDING_PRODUCT_CARD =
            $$(".swiper-slide ._UserProductCard_rzpr5_1")
                    .filter(visible)
                    .filter(not(text("Подарок за прохождение")))
                    .first();

    @Step("Клик по первому продукту (не onboarding) из 'Мои курсы'")
    public RenewalModalFlow openFirstNonOnboardingProductCard() {
        step("Ожидание загрузки страницы ЛК", () -> {
            WaitHelper.waitForPageLoad();
            WaitHelper.waitForAnimationComplete();
        });
        step("Клик по первому продукту без onboarding-бейджа", () ->
                UniversalMethod.clickButton(FIRST_NON_ONBOARDING_PRODUCT_CARD, "Первый продукт"));
        WaitHelper.waitForPageLoad();
        WaitHelper.waitForAnimationComplete();
        return this;
    }

    public String getUserEmail() {
        return userEmail;
    }
}
