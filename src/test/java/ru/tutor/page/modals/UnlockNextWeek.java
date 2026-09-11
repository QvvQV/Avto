package ru.tutor.page.modals;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Test;
import ru.tutor.constants.AddressPage;
import ru.tutor.utils.WaitHelper;
import ru.tutor.utils.UniversalMethod;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$x;

/**
 * Кнопки и логика модалки "Открой доступ к следующей неделе"
 */


public class UnlockNextWeek {

    // Названия кнопок
    private static final String OPEN_UNLIMITED_ACCESS_TEXT = "Открыть безлимитный доступ";
    private static final String BUY_TEXT = "Оплатить";
    private static final String GET_ACCESS_FOR_99_TEXT = "Оформить доступ за 99 рублей*";
    private static final String OTHER_OPTIONS_TEXT = "Другие варианты";
    private static final String OPEN_UNLIMITED_TEXT = "Открыть безлимит";
    private static final String PAY_3990_TEXT = "Оплатить 3 990 ₽";

    //Локаторы
    private final SelenideElement OPEN_UNLIMITED_ACCESS_BUTTON = $$("button").find(exactText(OPEN_UNLIMITED_ACCESS_TEXT));
    private final SelenideElement BUY_BUTTON = $$("button").find(exactText(BUY_TEXT));
    private final SelenideElement OTHER_OPTION_BUTTON = $x("//div[contains(@class, '_line_ls5ot_99')]//p[contains(text(), 'Другие варианты')]");
    private final SelenideElement OPEN_UNLIMITED_BUTTON =  $$("button").find(exactText(OPEN_UNLIMITED_TEXT));
    private final SelenideElement PAY_3990_BUTTON =  $$("button").find(exactText(PAY_3990_TEXT));
    private final SelenideElement GET_ACCESS_BUTTON = $$("button").find(exactText(GET_ACCESS_FOR_99_TEXT));

    public void openUnlimitedAccess() {
        WaitHelper.waitForAnimationComplete();
        if (OPEN_UNLIMITED_ACCESS_BUTTON.exists()) {
            UniversalMethod.clickButton(OPEN_UNLIMITED_ACCESS_BUTTON,OPEN_UNLIMITED_ACCESS_TEXT);
            System.out.println(BUY_BUTTON);
            UniversalMethod.clickButton(BUY_BUTTON,BUY_TEXT);
        } else {
            UniversalMethod.clickButton(OTHER_OPTION_BUTTON,OTHER_OPTIONS_TEXT);
            WaitHelper.waitForAnimationComplete();
            UniversalMethod.clickButton(OPEN_UNLIMITED_BUTTON,OPEN_UNLIMITED_TEXT);
            WaitHelper.waitForAnimationComplete();
            UniversalMethod.clickButton(PAY_3990_BUTTON,PAY_3990_TEXT);
            WaitHelper.waitForAnimationComplete();
        }
    }

    public void openLifetimeAccess() {
        WaitHelper.waitForAnimationComplete();
        UniversalMethod.clickButton(OPEN_UNLIMITED_ACCESS_BUTTON,OPEN_UNLIMITED_ACCESS_TEXT);
        WaitHelper.waitForAnimationComplete();
        UniversalMethod.clickButton(PAY_3990_BUTTON,PAY_3990_TEXT);
        WaitHelper.waitForAnimationComplete();
    }


    /**
     * Кнопка реневалл с управление доступом
     */

//    @Step("Переход в настройки платежей")
//    public void open() {
//        open(AddressPage.PAYMENTS_SETTINGS);
//        WaitHelper
//    }

    public void clickGetAccessFor99() {
        WaitHelper.waitForAnimationComplete();
        UniversalMethod.clickButton(GET_ACCESS_BUTTON,GET_ACCESS_FOR_99_TEXT);
        System.out.println(GET_ACCESS_BUTTON);
    }

}
