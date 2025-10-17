package ru.tutor.page.dashbords;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$$;

public class productCardEditor {

        private static final SelenideElement PRODUCT_CARD_EDITION = $$("p").find(exactText("Редактор карточек товара"));


    @Step("Кликаем по Редактор карточек товара")
    public static void clickFromProductCardEdition() {
        PRODUCT_CARD_EDITION.shouldBe(visible, Duration.ofSeconds(5))
                .shouldBe(interactable)
                .click();
        WaitHelper.waitForAnimationComplete();
        AllureAttachments.attachScreenshot("Результат после клика");
    }
}
