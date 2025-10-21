package ru.tutor.page.dashbords;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import ru.tutor.page.PersonalAccount.Home;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static io.qameta.allure.Allure.step;

public class productCardEditor {

    // Локаторы
    private static final SelenideElement PRODUCT_CARD_EDITION = $$("p").find(exactText("Редактор карточек товара"));
    private static final SelenideElement SHOOTING_TIPS_BUTTON = $("a[href*='tips']");
    private static final SelenideElement REDIRECT_TIPS_DASHBOARD = $("a[href*='dashboard']");
    private static final SelenideElement TITLE = $("h1");


    @Step("Кликаем по Редактор карточек товара")
    public static void clickFromProductCardEdition() {
        PRODUCT_CARD_EDITION.shouldBe(visible, Duration.ofSeconds(5))
                .shouldBe(interactable)
                .click();
        WaitHelper.waitForAnimationComplete();
        AllureAttachments.attachScreenshot("Результат после клика");
    }

    @Step("Переходим к советам")
    private static void clickShootingTips() {
        step("Кликаем по кнопке 'Советы по съемке'", () -> {
            textButton();
            SHOOTING_TIPS_BUTTON.shouldBe(visible, Duration.ofSeconds(3))
                    .shouldBe(interactable)
                    .click();
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

            WaitHelper.waitForUrlContains(originalUrl, Duration.ofSeconds(5));
            WaitHelper.waitForPageLoad();

            //Проверяем что мы на правильной странице
            TITLE.shouldBe(visible, Duration.ofSeconds(5));
            String titleText = TITLE.getText().trim();

            AllureAttachments.attachText("Заголовок страницы", titleText);
            AllureAttachments.attachScreenshot("Редирект страницы " + originalUrl);
        });
    }

    @Step("Проверяем работу в дашборде")
    public static void workDashboard() {
        redirectPage();
        step("Возвращаемся на страницу редактирования", () -> {
            REDIRECT_TIPS_DASHBOARD.shouldBe(visible, Duration.ofSeconds(3))
                    .click();

            WaitHelper.waitForAnimationComplete();
            WaitHelper.waitForPageLoad();

            String originalUrl = WebDriverRunner.url();

            AllureAttachments.attachText("Переход на страницу", originalUrl);
            AllureAttachments.attachScreenshot("Редирект страницы" + originalUrl);
        });

        step("Переход на главную страницу", () -> {
            Home.clickMainlink();
        });
    }

}
