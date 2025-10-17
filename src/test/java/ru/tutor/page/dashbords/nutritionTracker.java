package ru.tutor.page.dashbords;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$$;

public class nutritionTracker {
    private static final SelenideElement NUTRITION_TRACKER = $$("p").find(exactText("Трекер питания"));


    @Step("Кликаем по трекеру питания")
    public static void clickFromNutritionTracker() {
        NUTRITION_TRACKER.shouldBe(visible, Duration.ofSeconds(5))
                .shouldBe(interactable)
                .click();
        WaitHelper.waitForAnimationComplete();
        AllureAttachments.attachScreenshot("Результат после клика");
    }
}
