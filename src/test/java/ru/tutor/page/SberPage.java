package ru.tutor.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import net.datafaker.Faker;
import ru.tutor.config.TestConfig;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.TabManager;
import ru.tutor.utils.WaitHelper;
import ru.tutor.utils.universalMethod;

import java.time.Duration;
import java.util.Locale;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;

/**
 * Класс для локаторов и логики по платёжной системе Sber
 */

public class SberPage {
    private final Faker faker = new Faker(Locale.ENGLISH);

    // Текст Локаторов
    private final String BUY_TEXT = "начать обучение за 1 ₽*";
    private final String ERROR_MESSAGE_TEXT = "Произошла ошибка. Пожалуйста, попробуйте ещё раз.";
    private final String PAY_TEXT = "Оплатить";
    private final String SHOP_TEXT = "Перейти в магазин";
    private final String ERROR_TEXT = "Операция отклонена. Проверьте введенные данные, достаточность средств на карте и повторите операцию";

    //Локаторы
    private final SelenideElement BUY_BUTTON = $$("button").find(exactText(BUY_TEXT));
    private final SelenideElement ACCESS_BUTTON = $("#access-button");
    private final SelenideElement ERROR_MESSAGE = $$("p").find(exactText(ERROR_MESSAGE_TEXT));
    private final SelenideElement EMAIL_INPUT = $("[name='email']");
    private final SelenideElement CARD_NUMBER = $("#cardNumber");
    private final SelenideElement EXPIRY_DATE = $("#expiryDate");
    private final SelenideElement CVC = $("#cvc");
    private final SelenideElement SUCCESS_BUTTON = $$("button").find(exactText(PAY_TEXT));
    private final SelenideElement PASSWORD_EDIT = $("#passwordEdit");
    private final SelenideElement TEXT_ERROR = $$("span").find(exactText(ERROR_TEXT));
    private final SelenideElement GO_SHOP = $$("button").find(exactText(SHOP_TEXT));
    private final TtPage ttPage = new TtPage();

    private String email;

    @Step("Заполнение email и обработка модального окна")
    public void fillEmailAndProcessModal() {
        step("Ввод email и проверка отсутствия ошибок", () -> {
            email = faker.internet().emailAddress();
            EMAIL_INPUT.setValue(email);

            // Логика определения типа продукта
            String modalText = ttPage.getTextModal();
            logModalType(modalText, email);

            universalMethod.safeClick(CloudPage.FULL_OPTION, "Оплата");
            sleep(9000);
            ERROR_MESSAGE.shouldNotBe(visible);

            AllureAttachments.attachText("Email в модалке", email);
            System.out.println(modalText + ": " + email);
        });
    }

    public String getLastUsedEmail() {
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
    public void startOrderFromProduct() {
        step("Клик по кнопке оплатить", () ->
            universalMethod.safeClick(BUY_BUTTON, BUY_TEXT)
        );

        step("Заполнение email", () -> {
            String email = faker.internet().emailAddress();
            EMAIL_INPUT.setValue(email);
            universalMethod.safeClick(ACCESS_BUTTON, "Подтверждение");
            sleep(9000);

            ERROR_MESSAGE.shouldNotBe(visible);
            AllureAttachments.attachText("Оплата с продукта Excel", "Email: " + email);
            System.out.println("Оплата продукта с Excel: " + email);
        });

        TabManager.switchToNewWindow();
    }

    private void fillCardData(String cardNumber, String expiry, String cvc) {
        step("Заполнение данных карты: " + maskCardNumber(cardNumber), () -> {
            CARD_NUMBER.shouldBe(visible, Duration.ofSeconds(20)).setValue(cardNumber);
            EXPIRY_DATE.setValue(expiry);
            CVC.setValue(cvc);
            universalMethod.safeClick(SUCCESS_BUTTON, PAY_TEXT);

            AllureAttachments.attachText("Данные карты",
                    "Карта: " + maskCardNumber(cardNumber) + "\nСрок: " + expiry + "\nCVC: " + cvc);
        });
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber.length() > 8) {
            return cardNumber.substring(0, 4) + "********" + cardNumber.substring(cardNumber.length() - 4);
        }
        return cardNumber;
    }

    private void handleDeclinePayment() {
        step("Заполнение ACS кода", () -> {
            PASSWORD_EDIT.shouldBe(visible, Duration.ofSeconds(10)).setValue("111111");
            WaitHelper.waitForPageLoad();
            AllureAttachments.attachText("Статус оплаты", "Платеж отклонен");
        });
    }

    @Step("Оплата одобренной картой")
    public void payWithApprovedCard() {
        fillCardData(
                TestConfig.Cards.APPROVED,
                TestConfig.Cards.Approved.EXPIRY,
                TestConfig.Cards.Approved.CVC
        );
        step("Переход в магазин", () -> {
            universalMethod.safeClick(GO_SHOP, SHOP_TEXT);
            AllureAttachments.attachText("Статус оплаты", "Успешная оплата одобренной картой");
        });
    }

    @Step("Оплата картой с недостаточными средствами")
    public void payWithInsufficientFundsCard() {
        fillCardData(
                TestConfig.Cards.INSUFFICIENT_FUNDS,
                TestConfig.Cards.InsufficientFunds.EXPIRY,
                TestConfig.Cards.InsufficientFunds.CVC
        );

        handleDeclinePayment();
    }

    @Step("Оплата просроченной картой")
    public void payWithExpiredCard() {
        fillCardData(
                TestConfig.Cards.EXPIRED,
                TestConfig.Cards.Expired.EXPIRY,
                TestConfig.Cards.Expired.CVC
        );

        handleDeclinePayment();
    }

    @Step("Оплата картой с фиксированным балансом")
    public void payWithFixedBalanceCard() {
        fillCardData(
                TestConfig.Cards.FIXED_BALANCE,
                TestConfig.Cards.FixBalance.EXPIRY,
                TestConfig.Cards.FixBalance.CVC
        );
        handleDeclinePayment();

        step("Переход в магазин", () -> {
            universalMethod.safeClick(GO_SHOP, SHOP_TEXT);
            AllureAttachments.attachText("Статус оплаты", "Успешная оплата одобренной картой");
        });
    }

    public String getErrorText() {
        return TEXT_ERROR.shouldBe(visible, Duration.ofSeconds(10)).getText().trim();
    }
}