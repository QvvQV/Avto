package ru.tutor.page.personalAccount;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.UniversalMethod;
import ru.tutor.utils.WaitHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;

public class HotNovelties {

    private final SelenideElement NOVELTIES_TITLE = $x("//p[contains(., 'Горячие новинки')]");
    private final SelenideElement START_BUTTON = $x("//button[normalize-space(.)='Начать']");
    private final SelenideElement YES_BUTTON = $$("button").find(exactText("Да"));
    private final SelenideElement PREMIUM_99_BUTTON = $$("button").find(exactText("Получить Премиум доступ за 99₽"));

    @Step("Скролл до блока 'Горячие новинки'")
    public HotNovelties scrollToBlock() {
        step("Скролл до блока 'Горячие новинки'", () -> {
            NOVELTIES_TITLE.shouldBe(visible, Duration.ofSeconds(15)).scrollIntoView(true);
            WaitHelper.waitForAnimationComplete();
            AllureAttachments.attachScreenshot("Блок «Горячие новинки»");
        });
        return this;
    }

    @Step("Клик 'Начать' в первой карточке блока")
    public HotNovelties clickStartFirstCard() {
        step("Клик 'Начать' в первой карточке блока", () -> {
            START_BUTTON.shouldBe(visible, Duration.ofSeconds(15))
                    .click();
            WaitHelper.waitForAnimationComplete();
        });
        return this;
    }

    @Step("Клик 'Да'")
    public HotNovelties clickYes() {
        step("Клик 'Да' в модальном окне", () -> {
            UniversalMethod.clickButton(YES_BUTTON, "Да");
            WaitHelper.waitForAnimationComplete();
        });
        return this;
    }

    @Step("Клик 'Получить Премиум доступ за 99₽'")
    public HotNovelties clickPremiumAccess99() {
        step("Клик 'Получить Премиум доступ за 99₽'", () -> {
            UniversalMethod.clickButton(PREMIUM_99_BUTTON, "Получить Премиум доступ за 99₽");
            WaitHelper.waitForAnimationComplete();
        });
        return this;
    }
}