package ru.tutor.page.PersonalAccount;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import org.openqa.selenium.Keys;
import ru.tutor.page.dashbords.allDashbords;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;

public class Home {

    // Локаторы страницы
    private static final SelenideElement MAIN_LINK = $x("//div[text()='Главная']");
    private static final SelenideElement CATALOG_BUTTON = $$("button").find(exactText("Каталог курсов"));

    // Локаторы меню поиска
    private static final SelenideElement SEARCH = $("a.tutorial-step-mail + div");
    private static final SelenideElement SEARCH_INPUT = $("input[placeholder='Поиск...']");
    private static final SelenideElement PRODUCT_CARD_LIST = $("._ProductCardList_1s81r_1");


    // Добавление продукта
    private static final SelenideElement ADD_BUTTON = $$("button").find(exactText("Добавить"));
    private static final SelenideElement STAY_BUTTON = $$("button").find(exactText("Остаться"));

    // Локаторы в под-меню продукта
    private static final SelenideElement ELLIPSES_BUTTON = $("._control_sjghz_30");
    private static final SelenideElement GARBAGE_BUTTON = $("button._bin_sjghz_90");

    // Локаторы продукта в модальном окне
    private static final SelenideElement DELETE_BUTTON = $$("button").find(exactText("Удалить"));
    private static final SelenideElement CANCEL_BUTTON = $$("button").find(exactText("Отмена"));


    @Step("Навигация на главную страницу")
    public static void navigateToMainPage() {
        step("Переход на главную страницу", () -> {
            String originalUrl = WebDriverRunner.url();
            clickMainlink();

            verifyMainPageLoaded(originalUrl);
            AllureAttachments.attachScreenshot("Главная страница");
        });
    }

    public static void clickMainlink(){
        MAIN_LINK.shouldBe(visible, Duration.ofSeconds(5))
                    .shouldBe(interactable)
                    .click();
        WaitHelper.waitForPageLoad();
    }

    @Step("Проверка загрузки главной страницы")
    private static void verifyMainPageLoaded(String originalUrl) {
        step("валидация главной страницы", () -> {
            // Проверяем что URL изменился
            if (!WebDriverRunner.url().equals(originalUrl)) {
                AllureAttachments.attachText("Навигация",
                        "Переход выполнен: " + originalUrl + " → " + WebDriverRunner.url());
            }

            CATALOG_BUTTON.shouldBe(visible, Duration.ofSeconds(10));
            verifyHomeLinkPresence();

            AllureAttachments.attachText("Главная страница", "Страница успешно загружена и валидирована");
        });
    }

    @Step("Проверка доступности ссылки 'Главная'")
    public static void verifyHomeLinkPresence() {
        step("Валидация навигационного элемента", () -> {
            MAIN_LINK.shouldBe(visible, Duration.ofSeconds(5))
                    .shouldHave(exactText("Главная"));

            AllureAttachments.attachText("Навигация", "Ссылка 'Главная' доступна");
        });
    }

    @Step("Кликаем по кнопке поиск")
    private static void clickSearch() {
        step("Выбираем поиск", () ->
                SEARCH.shouldBe(visible, Duration.ofSeconds(3))
                        .shouldBe(interactable)
                        .click());
        WaitHelper.waitForAnimationComplete();
    }

    @Step("Вводим курс")
    private static void addCourse(String productName) {
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
    private static void clickFromCourse() {
        step("Кликаем по курсу", () -> {
            PRODUCT_CARD_LIST.shouldBe(visible, Duration.ofSeconds(5))
                    .shouldBe(interactable)
                    .click();
            WaitHelper.waitForAnimationComplete();
        });
    }

    @Step("Добавляем курс")
    private static void addCourse() {
        step("Добавляем продукт", () -> {
            ADD_BUTTON.shouldBe(visible, Duration.ofSeconds(5))
                    .shouldBe(interactable)
                    .click();
            WaitHelper.waitForAnimationComplete();
        });
    }

    @Step("Кликаем остаться")
    private static void stayClick(String productName) {
        step("Кликаем остаться", () -> {
            STAY_BUTTON.shouldBe(visible, Duration.ofSeconds(5))
                    .shouldBe(interactable)
                    .click();
            WaitHelper.waitForAnimationComplete();
            AllureAttachments.attachText("Продукт добавлен", productName);
        });
    }


    @Step("Прохождение добавления курса")
    public static void walkthroughtAddingACourse(String productName) {
        clickSearch();
        addCourse(productName);
        clickFromCourse();
        addCourse();
        stayClick(productName);
    }

    @Step("Удаляем курс")
    public static void deleteCourse() {
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
    public static void deleteProduct(String productName) {
        step("Удаляем продукт", () -> {
            refresh();
            allDashbords.safeCarouselClick(ELLIPSES_BUTTON);

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


    //    @Step("Вводим наименования курсов для проверки карусели приложения")
//    public static void enterCourseNames() {
//        step("Вводим в поисковик курс", () -> {
//            SEARCH.shouldBe(visible, Duration.ofSeconds(3))
//                            .shouldBe(interactable)
//                                    .click();
//            WaitHelper.waitForAnimationComplete();
//            SEARCH_INPUT.shouldBe(visible, Duration.ofSeconds(5)
//            ).setValue("Начинающий таролог");
//            PRODUCT_CLICK_FROM_SEARCH.shouldBe(visible)
//                .shouldBe(interactable)
//                .click();
//            WaitHelper.waitForAnimationComplete();
//        });
//        step("Добавляем курс", ()-> {
//            ADD_BUTTON.shouldBe(visible)
//                    .shouldBe(interactable)
//                    .click();
//            WaitHelper.waitForAnimationComplete();
//            STAY_BUTTON.shouldBe(visible)
//                    .shouldBe(interactable)
//                    .click();
//        });
//    }
}
