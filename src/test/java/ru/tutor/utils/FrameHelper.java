package ru.tutor.utils;

import io.qameta.allure.Step;
import java.util.function.Predicate;


import java.time.Duration;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class FrameHelper {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    @Step ("Переключение на iframe #{frameIndex}")
    public static void switchToFrame(int frameIndex){
        try {
            WaitHelper.waitFor(
                    () -> $$("iframe").size() > frameIndex,
                    DEFAULT_TIMEOUT,
                    String.format("Ожидание iframe #%d",  frameIndex)
            );

            int beforeCount = $$("iframe").size();
            switchTo().frame(frameIndex);
            WaitHelper.waitForPageLoad();
            WaitHelper.waitForAnimationComplete();

            AllureAttachments.attachText("Переключение iframe",
                    String.format("✓ iframe #%d (всего: %d)", frameIndex, beforeCount));
        } catch (Exception e) {
            AllureAttachments.attachText("Ошибка iframe",
                    String.format("✗ iframe #%d: %s", frameIndex, e.getMessage()));
            throw new AssertionError("Не удалось переключиться на iframe #" + frameIndex, e);
        }
    }

    @Step("Возврат на основную страницу")
    public static void switchToDefaultContent() {
        switchTo().defaultContent();
        WaitHelper.waitForPageLoad();
        AllureAttachments.attachText("Возврат", "✓ На основной странице");
    }

    @Step("Поиск iframe по условию")
    public static boolean switchToFrameByCondition(Predicate<String> condition) {
        int iframeCount = $$("iframe").size();

        for (int i = 0; i < iframeCount; i++) {
            try {
                switchTo().frame(i);
                WaitHelper.waitForPageLoad();

                String currentUrl = getWebDriver().getCurrentUrl();
                if (condition.test(currentUrl)) {
                    AllureAttachments.attachText("Найден iframe",
                            String.format("✓ iframe #%d: %s", i, currentUrl));
                    return true;
                }

                switchTo().defaultContent();
            } catch (Exception e) {
                switchTo().defaultContent();
            }
        }
        return false;
    }
}
