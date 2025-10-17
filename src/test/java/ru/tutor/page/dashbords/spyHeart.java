package ru.tutor.page.dashbords;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$$;

public class spyHeart {

        private static final SelenideElement SPY_HEART = $$("p").find(exactText("SpyHeart"));


    @Step("Кликаем по SpyHeart")
    public static void clickFromSpyHeart() {
        SPY_HEART.shouldBe(visible, Duration.ofSeconds(5))
                .shouldBe(interactable)
                .click();
        WaitHelper.waitForAnimationComplete();
        AllureAttachments.attachScreenshot("Результат после клика");
    }
}
