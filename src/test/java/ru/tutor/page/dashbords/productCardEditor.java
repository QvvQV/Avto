package ru.tutor.page.dashbords;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import ru.tutor.page.personalAccount.Home;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;
import ru.tutor.utils.universalMethod;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static io.qameta.allure.Allure.step;

public class productCardEditor {

    // Константа
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(20);

    // Локаторы
    private static final SelenideElement PRODUCT_CARD_EDITION = $$("p").find(exactText("Редактор карточек товара"));
    private static final SelenideElement SHOOTING_TIPS_BUTTON = $("a[href*='tips']");
    private static final SelenideElement REDIRECT_TIPS_DASHBOARD = $("a[href*='dashboard']");
    private static final SelenideElement TITLE = $("h1");


    @Step("Кликаем по Редактор карточек товара")
    public static void clickFromProductCardEdition() {
        universalMethod.safeClick(PRODUCT_CARD_EDITION,"Редактор карточек товара");
        AllureAttachments.attachScreenshot("Результат после клика");
    }

    @Step("Переходим к советам")
    private static void clickShootingTips() {
        step("Кликаем по кнопке 'Советы по съемке'", () -> {
            textButton();
            universalMethod.safeClick(SHOOTING_TIPS_BUTTON, "Тип");
        });
    }

    @Step("Текст кнопки")
    private static String textButton() {
        return SHOOTING_TIPS_BUTTON.getText().trim();
    }

    @Step("Проверка редиректа страницы")
    private static void redirectPage() {
        step("Проверка редиректа", () -> {
            String originalUrl = WebDriverRunner.url();
            clickShootingTips();

            WaitHelper.waitForUrlContains(originalUrl, DEFAULT_TIMEOUT);
            WaitHelper.waitForPageLoad();

            //Проверяем что мы на правильной странице
            TITLE.shouldBe(visible, DEFAULT_TIMEOUT);
            String titleText = TITLE.getText().trim();

            AllureAttachments.attachText("Заголовок страницы", titleText);
            AllureAttachments.attachScreenshot("Редирект страницы " + originalUrl);
        });
    }

    @Step("Проверяем работу в дашборде")
    public static void workDashboard() {
        redirectPage();
        step("Возвращаемся на страницу редактирования", () -> {
            universalMethod.safeClick(REDIRECT_TIPS_DASHBOARD, "Тип");

            WaitHelper.waitForPageLoad();

            String originalUrl = WebDriverRunner.url();

            AllureAttachments.attachText("Переход на страницу", originalUrl);
            AllureAttachments.attachScreenshot("Редирект страницы" + originalUrl);
        });

        step("Переход на главную страницу",
            Home::clickMainlink
        );
    }

}
