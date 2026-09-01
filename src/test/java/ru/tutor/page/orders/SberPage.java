package ru.tutor.page.orders;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import net.datafaker.Faker;
import ru.tutor.config.TestConfig;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.TabManager;
import ru.tutor.utils.WaitHelper;
import ru.tutor.utils.UniversalMethod;

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
    private final String CONFIRM = "Подтвердить";
    private final String BACK_TO_SHOP_TEXT = "Вернуться в магазин";

    // Таймаут
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(20);
    private final CloudPage cloudPage = new CloudPage();

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

    // Локатор плати частями
    private final SelenideElement PHONE_INPUT = $("input[type='tel'][class*='PhoneNumberInput']");
    private final SelenideElement SUBMIT_BUTTON = $$("button[type='submit']").find(exactText(CONFIRM));
    private final SelenideElement SMS_INPUT = $("input[type='tel'][class*='OtpInput']");
    private final SelenideElement FIO_INPUT = $("input[type='text'][placeholder='ФИО']");
    private final SelenideElement BIRTHDAY_INPUT = $("input[type='tel'][placeholder='Дата рождения']");
    private final SelenideElement BACK_TO_SHOP = $$("button").find(exactText(BACK_TO_SHOP_TEXT));
    private final SelenideElement CARD_INSTALLMENTS = $("input[data-test-id='pan']");
    private final SelenideElement EXPIRY_INSTALLMENTS = $("input[data-test-id='expiry']");
    private final SelenideElement CVC_INSTALLMENTS = $("input[data-test-id='cvc']");
    private final SelenideElement SUBMIT_INSTALLMENTS_BUTTON = $("button[data-test-id='submit-payment']");


    private String email;

    @Step("Заполнение email и обработка модального окна")
    public void fillEmailAndProcessModal() {
        step("Ввод email и проверка отсутствия ошибок", () -> {
            email = faker.internet().emailAddress();
            EMAIL_INPUT.setValue(email);

            // Логика определения типа продукта
            String modalText = ttPage.getTextModal();
            logModalType(modalText, email);

            UniversalMethod.safeClick(cloudPage.FULL_OPTION, "Оплата");
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
            case "Тариф «Лайт»":
                AllureAttachments.attachText("Оплата продукта", "Лайт тариф, email: " + email);
                break;
            case "Тариф «Премиум»":
                AllureAttachments.attachText("Оплата продукта", "Премиум тариф, email: " + email);
                break;
            case "Тариф «Навсегда»":
                AllureAttachments.attachText("Оплата продукта", "Пожизненный доступ, email: " + email);
                break;
            default:
                AllureAttachments.attachText("Оплата продукта", "Неизвестный тариф, email: " + email);
        }
    }

    private void fillCardData(String cardNumber, String expiry, String cvc) {
        step("Заполнение данных карты: " + maskCardNumber(cardNumber), () -> {
            CARD_NUMBER.shouldBe(visible, DEFAULT_TIMEOUT).setValue(cardNumber);
            EXPIRY_DATE.setValue(expiry);
            CVC.setValue(cvc);
            UniversalMethod.safeClick(SUCCESS_BUTTON, PAY_TEXT);

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
            PASSWORD_EDIT.shouldBe(visible, DEFAULT_TIMEOUT).setValue("111111");
            WaitHelper.waitForPageLoad();
            AllureAttachments.attachText("Статус оплаты", "Платеж отклонен");
        });
    }

    @Step("Оплата одобренной картой")
    public void payWithApprovedCard() {
        fillCardData(
                TestConfig.Cards.getApproved(),
                TestConfig.Cards.getApprovedExpiry(),
                TestConfig.Cards.getApprovedCvc()
        );
        step("Переход в магазин", () -> {
            UniversalMethod.safeClick(GO_SHOP, SHOP_TEXT);
            AllureAttachments.attachText("Статус оплаты", "Успешная оплата одобренной картой");
        });
    }

    @Step("Оплата картой с недостаточными средствами")
    public void payWithInsufficientFundsCard() {
        fillCardData(
                TestConfig.Cards.getInsufficientFunds(),
                TestConfig.Cards.getInsufficientFundsExpiry(),
                TestConfig.Cards.getInsufficientFundsCvc()
        );

        handleDeclinePayment();
    }

    @Step("Оплата просроченной картой")
    public void payWithExpiredCard() {
        fillCardData(
                TestConfig.Cards.getExpired(),
                TestConfig.Cards.getExpiredExpiry(),
                TestConfig.Cards.getExpiredCvc()
        );

        handleDeclinePayment();
    }

    @Step("Оплата картой с фиксированным балансом")
    public void payWithFixedBalanceCard() {
        fillCardData(
                TestConfig.Cards.getFixedBalance(),
                TestConfig.Cards.getFixedBalanceExpiry(),
                TestConfig.Cards.getFixedBalanceCvc()
        );
        handleDeclinePayment();

        step("Переход в магазин", () -> {
            UniversalMethod.safeClick(GO_SHOP, SHOP_TEXT);
            AllureAttachments.attachText("Статус оплаты", "Успешная оплата одобренной картой");
        });
    }

    public String getErrorText() {
        return TEXT_ERROR.shouldBe(visible, DEFAULT_TIMEOUT).getText().trim();
    }

    private void fillCardInstallmentData(String cardNumber, String expiry, String cvc) {
        step("Заполнение данных карты: " + maskCardNumber(cardNumber), () -> {
            CARD_INSTALLMENTS.shouldBe(visible, DEFAULT_TIMEOUT).setValue(cardNumber);
            EXPIRY_INSTALLMENTS.setValue(expiry);
            CVC_INSTALLMENTS.setValue(cvc);
            UniversalMethod.safeClick(SUBMIT_INSTALLMENTS_BUTTON, PAY_TEXT);

            AllureAttachments.attachText("Данные карты",
                    "Карта: " + maskCardNumber(cardNumber) + "\nСрок: " + expiry + "\nCVC: " + cvc);
        });
    }

    @Step("Заполнение номера телефона")
    private void fillPhone(Faker faker) {
        step("Ввод тестового номера телефона", () -> {
            String phone = "974 789-40-55";
            UniversalMethod.safeSetValue(PHONE_INPUT, phone, "Телефон");
            AllureAttachments.attachText("Телефон", phone);
            UniversalMethod.safeClick(SUBMIT_BUTTON, "Подтвердить телефон");
            WaitHelper.waitForPageLoad();
        });

    }

    @Step("Заполнение SMS-кода")
    private void fillSmsCode() {
        step("Ввод кода из SMS", () -> {
            UniversalMethod.safeSetValue(SMS_INPUT, "1111", "SMS-код");
            WaitHelper.waitForPageLoad();
        });

    }

    @Step("Заполнение ФИО и даты рождения")
    private void fillPersonalData(Faker faker) {
        step("Ввод персональных данных", () -> {
            String fullName = "Иванов Аркадий";
            UniversalMethod.safeSetValue(FIO_INPUT, fullName, "ФИО");
            UniversalMethod.safeSetValue(BIRTHDAY_INPUT, "02.02.2000", "Дата рождения");
            AllureAttachments.attachText("ФИО/Дата", fullName + " / " + "02.02.2000");
            UniversalMethod.safeClick(SUBMIT_BUTTON, "Подтвердить данные");
            WaitHelper.waitForPageLoad();
        });
    }

    @Step("Проверка кнопки 'Вернуться в магазин'")
    private void verifyBackToShop() {
        step("Ожидание завершения оплаты", () -> {
            BACK_TO_SHOP.shouldBe(visible, DEFAULT_TIMEOUT);
            AllureAttachments.attachScreenshot("Кнопка 'Вернуться в магазин'");
            AllureAttachments.attachText("Статус", "Оплата 'Плати частями' завершена успешно");
        });
    }

    @Step("Ожидание редиректа 'Плати частями' (поле телефона) и скриншот")
    public void verifyPartsPhoneForm() {
        step("Ожидание поля телефона после редиректа", () -> {
            PHONE_INPUT.shouldBe(visible, DEFAULT_TIMEOUT);
            AllureAttachments.attachScreenshot("Анкета 'Плати частями' (телефон)");
            AllureAttachments.attachText("Статус", "Редирект 'Плати частями' выполнен");
        });
    }

    @Step("Полный.flow 'Плати частями' (телефон -> SMS -> ФИО -> карта)")
    public void executeFullFlow() {
        fillPhone(faker);
        fillSmsCode();
//        fillPersonalData(faker);
        fillCardInstallmentData(
                TestConfig.Cards.getApproved(),
                TestConfig.Cards.getApprovedExpiry(),
                TestConfig.Cards.getApprovedCvc()
        );
        verifyBackToShop();
    }
}