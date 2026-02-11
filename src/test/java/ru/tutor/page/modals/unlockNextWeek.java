package ru.tutor.page.modals;

import com.codeborne.selenide.SelenideElement;
import ru.tutor.utils.WaitHelper;
import ru.tutor.utils.universalMethod;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$$;

/**
 * Кнопки и логика модалки "Открой доступ к следующей неделе"
 */


public class unlockNextWeek {

    // Названия кнопок
    private static final String OPEN_UNLIMITED_ACCESS_TEXT = "Открыть безлимитный доступ";
    private static final String BUY_TEXT = "Оплатить";

    //Локаторы
    private static final SelenideElement OPEN_UNLIMITED_ACCESS_BUTTON = $$("button").find(exactText(OPEN_UNLIMITED_ACCESS_TEXT));
    private static final SelenideElement BUY_BUTTON = $$("button").find(exactText(BUY_TEXT));

    public static void openUnlimitedAccess() {
        WaitHelper.waitForAnimationComplete();
        universalMethod.clickButton(OPEN_UNLIMITED_ACCESS_BUTTON,OPEN_UNLIMITED_ACCESS_TEXT);
        universalMethod.clickButton(BUY_BUTTON,BUY_TEXT);
    }
}
