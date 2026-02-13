package ru.tutor.utils;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$$;
import static io.qameta.allure.Allure.step;

public class RewardHelper {

    private static final SelenideElement COLLECT_YOUR_REWARD_BUTTON = $$("button").find(exactText("Забрать награду"));

    /**
     * Безопасная попытка клика на кнопку "Забрать награду"
     * Если кнопка есть - кликаем, если нет - пропускаем
     */

    public static void safeClickCollectReward() {
        step("Проверка и клик на кнопку 'Забрать награду'", () -> {
            if (isCollectRewardButtonPresent()) {
                collectRewardIfVisible();
            } else {
                AllureAttachments.attachText("Reward Button", "Кнопка 'Забрать награду' не найдена, пропускаем");
            }
        });
    }

    /**
     * Проверяет наличие кнопки без исключения
     */

    public static boolean isCollectRewardButtonPresent() {
        WaitHelper.waitForAnimationComplete();
        try {
            return COLLECT_YOUR_REWARD_BUTTON.exists() &&
                    COLLECT_YOUR_REWARD_BUTTON.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Кликает на кнопку если она видима
     */

    private static void collectRewardIfVisible() {
        step("Клик на кнопку 'Забрать награду'", () -> {
            universalMethod.safeClick(COLLECT_YOUR_REWARD_BUTTON, "Забрать награду");

            // Ждем исчезновения кнопки или модального окна
            WaitHelper.waitForModalToClose();
        });
    }
}
