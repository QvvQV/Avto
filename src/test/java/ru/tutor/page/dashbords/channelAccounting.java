package ru.tutor.page.dashbords;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$$;

public class channelAccounting {

        private static final SelenideElement CHANNEL_ACCOUNTING = $$("p").find(exactText("Учет каналов"));


    @Step("Кликаем по Учет каналов")
    public static void clickFromChannelAccounting() {
        CHANNEL_ACCOUNTING.shouldBe(visible, Duration.ofSeconds(5))
                .shouldBe(interactable)
                .click();
        WaitHelper.waitForAnimationComplete();
        AllureAttachments.attachScreenshot("Результат после клика");
    }
}
