package ru.tutor.page.modals;

import com.codeborne.selenide.SelenideElement;
import ru.tutor.utils.WaitHelper;
import ru.tutor.utils.UniversalMethod;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$$;

/**
 * Кнопки и логика модалки "Открой доступ к следующей неделе"
 */


public class UnlockNextWeek {

    // Названия кнопок
    private static final String OPEN_UNLIMITED_ACCESS_TEXT = "Открыть безлимитный доступ";
    private static final String BUY_TEXT = "Оплатить";

    //Локаторы
    private final SelenideElement OPEN_UNLIMITED_ACCESS_BUTTON = $$("button").find(exactText(OPEN_UNLIMITED_ACCESS_TEXT));
    private final SelenideElement BUY_BUTTON = $$("button").find(exactText(BUY_TEXT));

    public void openUnlimitedAccess() {
        WaitHelper.waitForAnimationComplete();
        UniversalMethod.clickButton(OPEN_UNLIMITED_ACCESS_BUTTON,OPEN_UNLIMITED_ACCESS_TEXT);
        System.out.println(BUY_BUTTON);
        UniversalMethod.clickButton(BUY_BUTTON,BUY_TEXT);
    }
}
