package ru.tutor.utils;

import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.sleep;
import static io.qameta.allure.Allure.step;

public class RewardHelper {

    private final SelenideElement COLLECT_YOUR_REWARD_BUTTON = $$("button").find(exactText("Забрать награду"));

    /**
     * Безопасная попытка клика на кнопку "Забрать награду"
     * Если кнопка есть - кликаем, если нет - пропускаем
     */

    public void safeClickCollectReward() {
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

    public boolean isCollectRewardButtonPresent() {
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

    private void collectRewardIfVisible() {
        step("Клик на кнопку 'Забрать награду'", () -> {
            UniversalMethod.safeClick(COLLECT_YOUR_REWARD_BUTTON, "Забрать награду");

            // Ждем исчезновения кнопки или модального окна
            WaitHelper.waitForModalToClose();
        });
    }

    public void safeClickCollectRewardIfAppears(Duration timeout) {
        step("Ожидание кнопки 'Забрать награду' до " + timeout.getSeconds() + " с", () -> {
            long end = System.currentTimeMillis() + timeout.toMillis();
            boolean appeared = false;
            while (System.currentTimeMillis() < end && !appeared) {
                appeared = isCollectRewardButtonPresent();
                if(!appeared) {
                    sleep(500);
                }
            }
            if (appeared) {
                step("Клик 'Забрать награду'", () -> {
                    UniversalMethod.safeClick(COLLECT_YOUR_REWARD_BUTTON, "Забрать награду");
                    WaitHelper.waitForModalToClose();
                });
            } else {
                AllureAttachments.attachText("Reward Button",
                        "Кнопка 'Забрать награду' не появилась за " + timeout.getSeconds() + " с");
            }
        });
    }
}
