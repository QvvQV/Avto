package ru.tutor.page.dashbords;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$$;
import static io.qameta.allure.Allure.step;

public class memoryTrainer {

        private static final SelenideElement MEMORY_TRAINER = $$("p").find(exactText("Тренажер памяти"));

    @Step("Кликаем по тренажёру памяти")
    public static void clickFromMemoryTrainer() {
        step("Кликаем по тренажёру памяти", ()->{
             MEMORY_TRAINER.shouldBe(visible, Duration.ofSeconds(5))
                .shouldBe(interactable)
                .click();
        WaitHelper.waitForAnimationComplete();
        AllureAttachments.attachScreenshot("Результат после клика");
        });
    }
}
