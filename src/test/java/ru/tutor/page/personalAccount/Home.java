package ru.tutor.page.personalAccount;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import org.openqa.selenium.Keys;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;
import ru.tutor.utils.UniversalMethod;

import java.time.Duration;
import java.util.Objects;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;

public class Home {

    // Локаторы страницы
    private final SelenideElement MAIN_LINK = $x("//div[text()='Главная']");
    private final SelenideElement CATALOG_BUTTON = $$("button").find(exactText("Каталог курсов"));

    // Локаторы меню поиска
    private final SelenideElement SEARCH = $("a.tutorial-step-mail + div");
    private final SelenideElement SEARCH_INPUT = $("input[placeholder='Поиск...']");
    private final SelenideElement PRODUCT_CARD_LIST = $("._ProductCardList_1s81r_1");


    // Добавление продукта
    private final SelenideElement ADD_BUTTON = $$("button").find(exactText("Добавить"));
    private final SelenideElement STAY_BUTTON = $$("button").find(exactText("Остаться"));

    // Локаторы в под-меню продукта
    private final SelenideElement ELLIPSES_BUTTON = $("._control_sjghz_30");
    private final SelenideElement GARBAGE_BUTTON = $("button._bin_sjghz_90");

    // Локаторы продукта в модальном окне
    private final SelenideElement DELETE_BUTTON = $$("button").find(exactText("Удалить"));
    private final SelenideElement CANCEL_BUTTON = $$("button").find(exactText("Отмена"));


    @Step("Навигация на главную страницу")
    public void navigateToMainPage() {
        step("Переход на главную страницу", () -> {
            String originalUrl = WebDriverRunner.url();
            clickMainlink();

            verifyMainPageLoaded(originalUrl);
            AllureAttachments.attachScreenshot("Главная страница");
        });
    }

    public void clickMainlink(){
        MAIN_LINK.shouldBe(visible, Duration.ofSeconds(5))
                    .shouldBe(interactable)
                    .click();
        WaitHelper.waitForPageLoad();
    }

    @Step("Проверка загрузки главной страницы")
    private void verifyMainPageLoaded(String originalUrl) {
        step("валидация главной страницы", () -> {
            // Проверяем что URL изменился
            if (!Objects.equals(WebDriverRunner.url(), originalUrl)) {
                AllureAttachments.attachText("Навигация",
                        "Переход выполнен: " + originalUrl + " → " + WebDriverRunner.url());
            }

            CATALOG_BUTTON.shouldBe(visible, Duration.ofSeconds(10));
            verifyHomeLinkPresence();

            AllureAttachments.attachText("Главная страница", "Страница успешно загружена и валидирована");
        });
    }

    @Step("Проверка доступности ссылки 'Главная'")
    public void verifyHomeLinkPresence() {
        step("Валидация навигационного элемента", () -> {
            MAIN_LINK.shouldBe(visible, Duration.ofSeconds(5))
                    .shouldHave(exactText("Главная"));

            AllureAttachments.attachText("Навигация", "Ссылка 'Главная' доступна");
        });
    }

    @Step("Кликаем по кнопке поиск")
    private void clickSearch() {
        step("Выбираем поиск", () ->
                SEARCH.shouldBe(visible, Duration.ofSeconds(3))
                        .shouldBe(interactable)
                        .click());
        WaitHelper.waitForAnimationComplete();
    }

    @Step("Вводим курс")
    private void addCourse(String productName) {
        step("Вводим курс: " + productName, () -> {
            SEARCH_INPUT.shouldBe(visible, Duration.ofSeconds(2))
                            .sendKeys(Keys.CONTROL + "a");
            SEARCH_INPUT.sendKeys(Keys.DELETE);
            SEARCH_INPUT.shouldBe(visible, Duration.ofSeconds(5))
                    .setValue(productName)
                    .pressEnter();
            WaitHelper.waitForAnimationComplete();
        });
    }

    @Step("Кликаем по курсу")
    private void clickFromCourse() {
        step("Кликаем по курсу", () -> {
            PRODUCT_CARD_LIST.shouldBe(visible, Duration.ofSeconds(5))
                    .shouldBe(interactable)
                    .click();
            WaitHelper.waitForAnimationComplete();
        });
    }

    @Step("Добавляем курс")
    private void addCourse() {
        step("Добавляем продукт", () -> {
            ADD_BUTTON.shouldBe(visible, Duration.ofSeconds(5))
                    .shouldBe(interactable)
                    .click();
            WaitHelper.waitForAnimationComplete();
        });
    }

    @Step("Кликаем остаться")
    private void stayClick(String productName) {
        step("Кликаем остаться", () -> {
            STAY_BUTTON.shouldBe(visible, Duration.ofSeconds(5))
                    .shouldBe(interactable)
                    .click();
            WaitHelper.waitForAnimationComplete();
            AllureAttachments.attachText("Продукт добавлен", productName);
        });
    }


    @Step("Прохождение добавления курса")
    public void walkthroughtAddingACourse(String productName) {
        clickSearch();
        addCourse(productName);
        clickFromCourse();
        addCourse();
        stayClick(productName);
    }

    @Step("Удаляем курс")
    public void deleteCourse() {
        step("Кликаем по картинке для скрытого меню", () -> {
            ELLIPSES_BUTTON.shouldBe(visible, Duration.ofSeconds(5))
                    .shouldBe(interactable)
                    .click();
            WaitHelper.waitForAnimationComplete();
        });
        step("Кликаем по корзине", () -> {
            GARBAGE_BUTTON.shouldBe(visible, Duration.ofSeconds(3))
                    .shouldBe(interactable)
                    .click();
            WaitHelper.waitForAnimationComplete();
            AllureAttachments.attachScreenshot("Модальное окно удаления");
        });
        step("В модальном окне кликаем 'Удалить'", () -> {
            DELETE_BUTTON.shouldBe(visible, Duration.ofSeconds(3))
                    .shouldBe(interactable)
                    .click();
            WaitHelper.waitForAnimationComplete();
        });
    }

    @Step("Удаляем продукт")
    public void deleteProduct(String productName) {
        step("Удаляем продукт", () -> {
            refresh();
            UniversalMethod.safeClick(ELLIPSES_BUTTON, "Круг");

            GARBAGE_BUTTON.shouldBe(visible, Duration.ofSeconds(3))
                    .shouldBe(interactable)
                    .click();
            WaitHelper.waitForAnimationComplete();
            AllureAttachments.attachScreenshot("Появление модального окна");
        });
        step("Появление мод окна и завершение удаления", () -> {
            DELETE_BUTTON.shouldBe(visible, Duration.ofSeconds(3))
                    .shouldBe(enabled)
                    .click();
            WaitHelper.waitForAnimationComplete();
        });
        AllureAttachments.attachText("Курс удалён", "✅ " + productName);
        AllureAttachments.attachScreenshot("Главная страница после удаления продукта");
    }
}
