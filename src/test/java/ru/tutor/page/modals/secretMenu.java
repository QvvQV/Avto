package ru.tutor.page.modals;

import com.codeborne.selenide.SelenideElement;
import ru.tutor.utils.WaitHelper;
import ru.tutor.utils.universalMethod;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static io.qameta.allure.Allure.step;

/**
 * Кнопки "Выезжающего меню"
 */


public class secretMenu {

    // Названия кнопок
    private static final String EXIT_TEXT = "Выход";
    private static final String EXIT_TWO_TEXT = "Выйти";

    //Локаторы
    private static final SelenideElement OPEN_MENU_BUTTON = $("img[alt='user']");
    private static final SelenideElement EXIT_BUTTON = $$("button").find(exactText(EXIT_TEXT));
    private static final SelenideElement EXIT_TWO_BUTTON = $$("button").find(exactText(EXIT_TWO_TEXT));

    private static void openUserMenu() {
        universalMethod.safeClick(OPEN_MENU_BUTTON, "Клик по выпадающему меню");
    }

    public static void logoutCurrentUser() {
        step("Выход из личного кабинета пользователя", () -> {
            openUserMenu();
            WaitHelper.waitForAnimationComplete();
            universalMethod.safeClick(EXIT_BUTTON, EXIT_TEXT);
            WaitHelper.waitForAnimationComplete();
            universalMethod.safeClick(EXIT_TWO_BUTTON, EXIT_TEXT);
            WaitHelper.waitForAnimationComplete();
            WaitHelper.waitForPageLoad();
        });
    }


}
