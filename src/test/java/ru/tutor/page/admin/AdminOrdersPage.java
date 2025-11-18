package ru.tutor.page.admin;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import ru.tutor.ProductConfiguration;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;

public class AdminOrdersPage {

    private final AdminOrderDetails adminOrderDetails = new AdminOrderDetails();

    private static final String APPLY_TEXT = "Применить";
    private static final String MAKE_A_PAYMENT_TEXT = "Провести платеж";
    private static final String SAVE_TEXT = "Сохранить";

    private static SelenideElement ORDERS_BUTTON = $("a[href*='orders']");
    private static SelenideElement APPLY_BUTTON = $$("button").find(exactText(APPLY_TEXT));
    private static SelenideElement MAKE_A_PAYMENT = $(".button-modal");
    private static SelenideElement SAVE_BUTTON = $$("button").find(exactText(SAVE_TEXT));
    private static SelenideElement EMAIL_INPUT_SEARCH = $("input[placeholder='Email']");
    private static SelenideElement ORDER_NUMBER = $("a[data-discover='true'][href*='/order/']");
    private static SelenideElement SUM_INPUT = $$("input[type=text]").get(0);


    // Селектор для поиска Лайт продуктов
    private static ElementsCollection ALL_ORDER_LINKS = $$("a[data-discover='true'][href*='/order/']");
    private static SelenideElement getLiteOrderLink() {
        return ALL_ORDER_LINKS.findBy(text("Лайт").or(text("Lite")));
    }


    // Универсальные методы
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    /**
     * Универсальный метод для клика по кнопке с проверками
     */
    @Step("Клик по кнопке: {buttonName}")
    private static void clickButton(SelenideElement button, String buttonName) {
        step("Клик по кнопке '" + buttonName + "'", () -> {
            button.scrollTo()
                    .shouldBe(visible, DEFAULT_TIMEOUT)
                    .shouldBe(interactable, DEFAULT_TIMEOUT)
                    .click();

            WaitHelper.waitForAnimationComplete();
            AllureAttachments.attachText("Кнопка нажата", "✅ " + buttonName);
        });
    }

    /**
     * Универсальный метод для клика по кнопке с ожиданием загрузки страницы
     */
    @Step("Клик по кнопке с переходом: {buttonName}")
    private static void clickButtonWithPageLoad(SelenideElement button, String buttonName) {
        step("Клик по кнопке '" + buttonName + "' с переходом", () -> {
//            String originalUrl = WebDriverRunner.url();

            button.scrollTo()
                    .shouldBe(visible, DEFAULT_TIMEOUT)
                    .shouldBe(interactable, DEFAULT_TIMEOUT)
                    .click();

            WaitHelper.waitForPageLoad();
            WaitHelper.waitForAnimationComplete();

//            if (!WebDriverRunner.url().equals(originalUrl)) {
//                AllureAttachments.attachText("Переход",
//                        "Успешный переход после клика на: " + buttonName);
//            }

            AllureAttachments.attachText("Кнопка нажата", "✅ " + buttonName + " - появилось модальное окно");
        });
    }

    @Step("Переход в раздел заказов")
    public AdminOrdersPage navigateToOrders() {
       return step("Переход в раздел заказов", ()-> {
           ORDERS_BUTTON.shouldBe(visible, DEFAULT_TIMEOUT)
                .shouldBe(interactable)
                .click();
        WaitHelper.waitForPageLoad();
        WaitHelper.waitForAnimationComplete();
        return this;
       });
    }


    @Step("Поиск заказа по email: {email}")
    public AdminOrdersPage findOrderByEmail(String email) {
        return step("Поиск заказа по email", ()->{
               EMAIL_INPUT_SEARCH.shouldBe(visible, DEFAULT_TIMEOUT)
                .setValue(email);
        clickButton(APPLY_BUTTON, APPLY_TEXT);
        WaitHelper.waitForAnimationComplete();

        ORDER_NUMBER.shouldBe(visible, DEFAULT_TIMEOUT);
        AllureAttachments.attachText("Поиск заказа", "Заказ для email " + email + " найден");

        return this;
        });
    }

    @Step("Переход в заказ для рекуррентного платежа")
    public void processRecurrentPayment() {
        int originalWindowCount = WebDriverRunner.getWebDriver().getWindowHandles().size();

        ORDER_NUMBER.click();
        WaitHelper.waitForAnimationComplete();

        Selenide.Wait().until(driver -> driver.getWindowHandles().size() > originalWindowCount);

        switchTo().window(originalWindowCount);
        WaitHelper.waitForAnimationComplete();
        WaitHelper.waitForPageLoad();

// переход на др страницу
        step("Проверяем что находимся на странице заказа", () -> {
            String currentUrl = WebDriverRunner.url();
            assert currentUrl.contains("/order/") :
                    "Не удалось перейти на страницу заказа. Текущий URL: " + currentUrl;
            AllureAttachments.attachText("URL заказа: ", currentUrl);
        });

    }

    @Step("Проводим оплату")
    public void makePayment(String productId) {
        clickButton(MAKE_A_PAYMENT, MAKE_A_PAYMENT_TEXT);

        String price = ProductConfiguration.getSettings(productId).getPrice();

        SUM_INPUT.shouldBe(visible, DEFAULT_TIMEOUT).setValue(price);
        clickButton(SAVE_BUTTON, SAVE_TEXT);

        AllureAttachments.attachText("Рекуррент",
        "Рекуррентный платеж успешно проведен. ID продукта: " + productId + ", цена: " + price);
    }

//        @Step("Автоматический переход в подходящий заказ для рекуррента")
//    public void processRecurrentPaymentAuto() {
//        step("поиск Лайт заказа внутри основного заказа", () -> {
//            SelenideElement liteOrderLink = findLiteOrderInCurrentOrder();
//
//            if (liteOrderLink.exists()) {
//                AllureAttachments.attachText("Автовыбор", "Найден Лайт заказ внутри основного заказа - переходим в него");
//                processRecurrentPaymentForElement(liteOrderLink, "Лайт");
//            } else {
//                AllureAttachments.attachText("Автовыбор", "Лайт заказ не найден - работаем с текущим заказом");
//                AllureAttachments.attachText("Текущий заказ", "Работаем с основным заказом");
//            }
//        });
//    }

    @Step("Поиск Лайт заказа внутри текущего заказа")
    private SelenideElement findLiteOrderInCurrentOrder() {
        return $$("a[href*='/order/']")
            .findBy(text("Лайт").or(text("Lite")).or(text("лайт")).or(text("lite")));
    }
}
