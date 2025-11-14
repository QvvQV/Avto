package ru.tutor.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import net.datafaker.Faker;
import ru.tutor.config.TestConfig;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;

import java.time.Duration;
import java.util.Locale;
import java.util.Set;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;

public class SberPage {
    private static final Faker faker = new Faker(new Locale("en"));

    //Локаторы
    private static final SelenideElement BUY_BUTTON = $$("button").find(exactText("начать обучение за 1 ₽*"));
    private static final SelenideElement ACCESS_BUTTON = $("#access-button");
    private static final SelenideElement ERROR_MESSAGE = $$("p").find(exactText("Произошла ошибка. Пожалуйста, попробуйте ещё раз."));
    private static final SelenideElement EMAIL_INPUT = $("[name=\"email\"]");
    private static final SelenideElement CARD_NUMBER = $("#cardNumber");
    private static final SelenideElement EXPIRY_DATE = $("#expiryDate");
    private static final SelenideElement CVC = $("#cvc");
    private static final SelenideElement SUCCESS_BUTTON = $$("button").find(exactText("Оплатить"));
    private static final SelenideElement PASSWORD_EDIT = $("#passwordEdit");
    private static final SelenideElement TEXT_ERROR = $$("span").find(exactText("Операция отклонена. Проверьте введенные данные, достаточность средств на карте и повторите операцию"));
    private static final SelenideElement GO_SHOP = $$("button").find(exactText("Перейти в магазин"));

    private static String email;

    @Step("Переключение на новое окно браузера")
    public static void switchToNewWindow() {
        String currentWindowHandle = webdriver().driver().getWebDriver().getWindowHandle();
        Set<String> windowHandles = webdriver().driver().getWebDriver().getWindowHandles();

        for (String windowHandle : windowHandles) {

            if (!windowHandle.equals(currentWindowHandle)) {
                webdriver().driver().getWebDriver().switchTo().window(windowHandle);
                AllureAttachments.attachText("Переключение окна",
                        "Переключено на окно: " + windowHandle);
                break;
            }
        }
    }

    @Step("Заполнение email и обработка модального окна")
    public static void fillEmailAndProcessModal() {
        step("Ввод email и проверка отсутствия ошибок", () -> {
            email = faker.internet().emailAddress();
            EMAIL_INPUT.setValue(email);

            // Логика определения типа продукта
            String modalText = ttPage.setTextFromModal();
            logModalType(modalText, email);

            CloudPage.FULL_OPTION.shouldBe(visible).click();
            sleep(9000);
            ERROR_MESSAGE.shouldNotBe(visible);

            AllureAttachments.attachText("Email в модалке", email);
            System.out.println(modalText + ": " + email);
        });
    }

    public static String getLastUsedEmail() {
        return email;
    }

    public static void logModalType(String modalText, String email) {
        switch (modalText) {
            case "ПОПРОБУЙ ПЕРВУЮ НЕДЕЛЮ ТАРИФА “ЛАЙТ”":
                AllureAttachments.attachText("Оплата продукта", "Лайт тариф, email: " + email);
                break;
            case "ПОПРОБУЙ ПЕРВУЮ НЕДЕЛЮ ТАРИФА “ПРЕМИУМ”":
                AllureAttachments.attachText("Оплата продукта", "Премиум тариф, email: " + email);
                break;
            case "ОФОРМИ ПОЖИЗНЕННЫЙ ДОСТУП К БОЛЕЕ ЧЕМ К 400 КУРСАМ":
                AllureAttachments.attachText("Оплата продукта", "Пожизненный доступ, email: " + email);
                break;
            default:
                AllureAttachments.attachText("Оплата продукта", "Неизвестный тариф, email: " + email);
        }
    }

    @Step("Начало оформления заказа с продукта Excel")
    public static void startOrderFromProduct() {
        step("Клик по кнопке оплатить", () -> {
            BUY_BUTTON.shouldBe(visible, Duration.ofSeconds(10)).click();
        });

        step("Заполнение email", () -> {
            String email = faker.internet().emailAddress();
            EMAIL_INPUT.setValue(email);
            ACCESS_BUTTON.shouldBe(visible).click();
            sleep(9000);

            ERROR_MESSAGE.shouldNotBe(visible);
            AllureAttachments.attachText("Оплата с продукта Excel", "Email: " + email);
            System.out.println("Оплата продукта с Excel: " + email);
        });

        switchToNewWindow();
    }

    private static void fillCardData(String cardNumber, String expiry, String cvc) {
        step("Заполнение данных карты: " + maskCardNumber(cardNumber), () -> {
            CARD_NUMBER.shouldBe(visible, Duration.ofSeconds(20)).setValue(cardNumber);
            EXPIRY_DATE.setValue(expiry);
            CVC.setValue(cvc);
            SUCCESS_BUTTON.shouldBe(visible).click();

            AllureAttachments.attachText("Данные карты",
                    "Карта: " + maskCardNumber(cardNumber) + "\nСрок: " + expiry + "\nCVC: " + cvc);
        });
    }

    private static String maskCardNumber(String cardNumber) {
        if (cardNumber.length() > 8) {
            return cardNumber.substring(0, 4) + "********" + cardNumber.substring(cardNumber.length() - 4);
        }
        return cardNumber;
    }

    private static void handleDeclinePayment() {
        step("Заполнение ACS кода", () -> {
            PASSWORD_EDIT.shouldBe(visible, Duration.ofSeconds(10)).setValue("111111");
            WaitHelper.waitForPageLoad();
            AllureAttachments.attachText("Статус оплаты", "Платеж отклонен");
        });
    }

    @Step("Оплата одобренной картой")
    public static void payWithApprovedCard() {
        fillCardData(
                TestConfig.Cards.APPROVED,
                TestConfig.Cards.Approved.EXPIRY,
                TestConfig.Cards.Approved.CVC
        );
        step("Переход в магазин", () -> {
            GO_SHOP.shouldBe(visible, Duration.ofSeconds(15)).click();
            AllureAttachments.attachText("Статус оплаты", "Успешная оплата одобренной картой");
        });
    }

    @Step("Оплата картой с недостаточными средствами")
    public static void payWithInsufficientFundsCard() {
        fillCardData(
                TestConfig.Cards.INSUFFICIENT_FUNDS,
                TestConfig.Cards.InsufficientFunds.EXPIRY,
                TestConfig.Cards.InsufficientFunds.CVC
        );

        handleDeclinePayment();
    }

    @Step("Оплата просроченной картой")
    public static void payWithExpiredCard() {
        fillCardData(
                TestConfig.Cards.EXPIRED,
                TestConfig.Cards.Expired.EXPIRY,
                TestConfig.Cards.Expired.CVC
        );

        handleDeclinePayment();
    }

    @Step("Оплата картой с фиксированным балансом")
    public static void payWithFixedBalanceCard() {
        fillCardData(
                TestConfig.Cards.FIXED_BALANCE,
                TestConfig.Cards.FixBalance.EXPIRY,
                TestConfig.Cards.FixBalance.CVC
        );
        handleDeclinePayment();

        step("Переход в магазин", () -> {
            GO_SHOP.shouldBe(visible, Duration.ofSeconds(15)).click();
            AllureAttachments.attachText("Статус оплаты", "Успешная оплата одобренной картой");
        });
    }

    public static String getErrorText() {
        return TEXT_ERROR.shouldBe(visible, Duration.ofSeconds(10)).getText().trim();
    }
}