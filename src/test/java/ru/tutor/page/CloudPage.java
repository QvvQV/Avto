package ru.tutor.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import net.datafaker.Faker;
import org.openqa.selenium.By;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;

import java.time.Duration;
import java.util.Locale;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import static ru.tutor.page.SberPage.logModalType;

public class CloudPage {
    private static final Faker faker = new Faker(new Locale("en"));

    //Локаторы
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

    //Тестовые данные
    private static final String TEST_CARD_NUMBER = "42424242424242420745555";

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