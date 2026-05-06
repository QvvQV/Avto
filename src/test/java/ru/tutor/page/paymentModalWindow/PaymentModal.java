package ru.tutor.page.paymentModalWindow;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.TabManager;
import ru.tutor.utils.WaitHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PaymentModal {

    private final SelenideElement PRICE_ELEMENT = $("div[class*='price']");
    private final SelenideElement OPEN_ACCESS_BUTTON = $$("button").find(exactText("Открыть доступ"));

    private String getPriceText() {
        return PRICE_ELEMENT.getText().replace("\u00A0", " ");
    }

    @Step("Проверяем что цена '1 рубль'")
    public void verifyPriceIsOneRuble() {
        step("Сравниваем текст в модальном окне", ()->{
           String actualText = getPriceText();
           assertTrue(actualText.contains("ВСЕГО ЗА 1") && actualText.contains("₽*"),
            "Текст цены не соответствует ожидаемому. Фактический текст: " + actualText);
        });
        step("Инициализация оплаты",()->{
            OPEN_ACCESS_BUTTON.shouldBe(visible)
                    .shouldBe(interactable)
                    .click();
            TabManager.switchToPaymentPageAndVerify();
        });

        step("Заполнение данных карты",
                () -> new CloudPage().fillingWithTestData()
        );
        step("Возврат на сайт после оплаты", () -> {

            closeWindow();
            switchTo().window(0);

            WaitHelper.waitForUrlContains("https://client.ttplace.ru/user/main", Duration.ofSeconds(10));
            AllureAttachments.attachText("Статус", "Успешное вовзращение на сайт после оплаты");
        });
    }
}
