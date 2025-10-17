package ru.tutor.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import net.datafaker.Faker;
import org.openqa.selenium.By;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;

import java.time.Duration;
import java.util.Locale;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.tutor.page.SberPage.logModalType;

public class CloudPage {
    private static final Faker faker = new Faker(new Locale("en"));

    //Локаторы в модальном окне
    private static SelenideElement BUY_BUTTON = $$("button").find(exactText("начать обучение за 1 ₽*"));
    private static SelenideElement ACCESS_BUTTON = $(By.id("access-button"));
    private static SelenideElement ERROR_MESSAGE = $$("p").find(exactText("Произошла ошибка. Пожалуйста, попробуйте ещё раз."));
    private static SelenideElement EMAIL_INPUT = $("[name=\"email\"]");
    private static SelenideElement CARD_PAGE_BUTTON = $("[data-test=\"cardpay-page-button\"]");
    private static SelenideElement CARD_INPUT = $("[automation-id=\"tui-input-card-group__card\"]");
    private static SelenideElement CARD_PAY_BUTTON = $("[data-test=\"cardpay-button\"]");
    private static SelenideElement SUCCESS_BUTTON = $$("button").find(exactText("Успех"));
    public static SelenideElement FULL_OPTION = $("[name=\"full\"]");
    private static SelenideElement INSTALLMENTS_OPTION = $("[name=\"installments\"]");

    //Локаторы в отдельном окне
    private static SelenideElement CARD_INPUT_NEW_WINDOW = $(By.id("card"));
    private static SelenideElement DATE_INPUT = $(By.id("date"));
    private static SelenideElement CVV_INPUT = $(By.id("cvv"));
    private static SelenideElement CONFIRM_BUTTON = $("button[data-v-59c9a1ad]");
    private static SelenideElement BACK_TO_SHOP_BUTTON = $("button[data-v-c1ac6fa4]");

    //Тестовые данные
    private static final String TEST_CARD_NUMBER = "42424242424242420745555";
    private static final String TEST_CARD = "4242424242424242";
    private static final String TEST_DATE = "0745";
    private static final String TEST_CVV = "555";

    @Step("Заполнение данных модального окна")
    public static void clickBtnStart() {
        step("Ввод email и выбор тарифа", () -> {
            BUY_BUTTON.shouldBe(visible).click();
            EMAIL_INPUT.sendKeys(faker.internet().emailAddress());
            ACCESS_BUTTON.shouldBe(visible).click();
            ERROR_MESSAGE.shouldNot(visible);
        });
    }

    public static String atrEmail() {
        return EMAIL_INPUT.getDomAttribute("value");
    }

    // ввод банковских данных в модалку Cloud
    @Step("Заполнение данных модального окна")
    public static void clickCardPageBtn() {
        step("ввод валидных банковских данных", () -> {
            sleep(2000);
            switchTo().frame(0);
            WaitHelper.waitForPageLoad();
            CARD_PAGE_BUTTON.shouldBe(visible).click();
            CARD_INPUT.shouldBe(visible, Duration.ofSeconds(10)).sendKeys(TEST_CARD_NUMBER);
            CARD_PAY_BUTTON.click();
            switchTo().frame(0);
            SUCCESS_BUTTON.shouldBe(visible).click();
        });
    }

    public static void setDateModal() {
        step("Ввод email и проверка отсутствия ошибок", () -> {
            String email = faker.internet().emailAddress();
            EMAIL_INPUT.setValue(email);

            String modalText = ttPage.setTextFromModal();
            logModalType(modalText, email);
            FULL_OPTION.shouldBe(visible).click();
            ERROR_MESSAGE.shouldNot(visible);

            AllureAttachments.attachText("Email в модалке", email);
            System.out.println(modalText + ": " + email);
        });
    }

    public static void setDateModalInstallments() {
        step("Ввод email и проверка отсутствия ошибок", () -> {
            String email = faker.internet().emailAddress();
            EMAIL_INPUT.setValue(email);

            String modalText = ttPage.setTextFromModal();
            logModalType(modalText, email);
            INSTALLMENTS_OPTION.shouldBe(visible).click();
            ERROR_MESSAGE.shouldNot(visible);

            AllureAttachments.attachText("Email в модалке", email);
            System.out.println(modalText + ": " + email);
        });
    }

    @Step("Наполнение тестовыми данными")
    public static void fillingWithTestData() {
        step("Ввод номера карты", () ->
                CARD_INPUT_NEW_WINDOW.shouldBe(visible, Duration.ofSeconds(5)).setValue(TEST_CARD));
        step("Ввод срока действия:", () ->
                DATE_INPUT.shouldBe(visible, Duration.ofSeconds(5)).setValue(TEST_DATE));
        step("Ввод CVV:", () ->
            CVV_INPUT.setValue(TEST_CVV));

        step("Отправка формы оплаты", () ->
                CONFIRM_BUTTON.shouldBe(visible, Duration.ofSeconds(3))
                        .shouldBe(enabled)
                        .click());

        AllureAttachments.attachText("Введённые банковские данные: ", "\nНомер: " + TEST_CARD +
                    "\nСрок действия: " + TEST_DATE + "\nCVV: " + TEST_CVV);

        WaitHelper.waitForAnimationComplete();
        step("Подтверждение заказа", () -> {
            switchTo().frame(0);
            CONFIRM_BUTTON.shouldBe(visible, Duration.ofSeconds(3))
                    .click();

            WaitHelper.waitForAnimationComplete();
            WaitHelper.waitForPageLoad();

            switchTo().defaultContent();

            String actualText = BACK_TO_SHOP_BUTTON.getText().trim();
            String expectedText = "Вернуться в магазин";

            assertEquals(expectedText, actualText,
                    String.format("Текст кнопки не совпадает. Ожидалось: '%s', Фактически: '%s'",
                            expectedText, actualText));

            AllureAttachments.attachText("Проверка текста кнопки",
                    "✓ Текст кнопки корректен: " + actualText);
            AllureAttachments.attachScreenshot("Страница после оплаты");
        });
    }

    //    public static void clickBtnStart(){
//        step("Клик по кнопке и ввод email", () -> {
//            BUY_BUTTON.shouldBe(visible, Duration.ofSeconds(10)).click();
//            String email = faker.internet().emailAddress();
//            EMAIL_INPUT.setValue(email);
//            ACCESS_BUTTON.shouldBe(visible).click();
//            ERROR_MESSAGE.shouldNotBe(visible);
//            AllureAttachments.attachText("Введён email", email);
//        });
//    }
//
//    public static String artEmail(){
//        return EMAIL_INPUT.getAttribute("value");
//    }
//
//    @Step("Ввод банковских данных в модалку Cloud")
//    public static void clickCardPageBtn(){
//        step("Заполнение данных карты",()->{
//            WaitHelper.waitForPageLoad();
//
//            switchToPaymentFrame();
//            CARD_PAGE_BUTTON.shouldBe(visible, Duration.ofSeconds(10)).click();
//            CARD_INPUT.sendKeys(TEST_CARD_NUMBER);
//            CARD_PAY_BUTTON.click();
//            switchToPaymentFrame();
//            SUCCESS_BUTTON.shouldBe(visible, Duration.ofSeconds(10)).click();
//
//            AllureAttachments.attachText("Оплата картой", "Введён номер карты: " + TEST_CARD_NUMBER);
//        });
//    }
//
//    private static void switchToPaymentFrame(){
//        sleep(2000);
//        switchTo().frame(0);
//        WaitHelper.waitForPageLoad();
//    }
//
//    @Step("Заполнение данных модального окна")
//    public static void setDateModal(){
//        step("Ввод email и выбор тарифа", () ->{
//            String email = faker.internet().emailAddress();
//            EMAIL_INPUT.setValue(email);
//            FULL_OPTION.shouldBe(visible).click();
//            ERROR_MESSAGE.shouldNotBe(visible);
//            AllureAttachments.attachText("Email для рассрочки", email);
//        });
//    }
//
//    @Step("Ожидание редиеркта после оплаты")
//    public static void waitForPaymentRedirect(){
//        WaitHelper.waitForPaymentProcessing();
//        AllureAttachments.attachText("Статус", "Редирект после оплаты обработан");
//    }
}