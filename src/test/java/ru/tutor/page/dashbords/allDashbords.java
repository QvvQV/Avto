package ru.tutor.page.dashbords;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.tutor.page.PersonalAccount.Home;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class allDashbords {

    //локаторы модуля Приложения

    private static final SelenideElement APPLICATION = $$("h2").find(exactText("Приложения"));
    private static final SelenideElement NUTRITION_TRACKER = $$("p").find(exactText("Трекер питания"));
    private static final SelenideElement NATAL_CHART = $$("p").find(exactText("Натальная карта"));
    private static final SelenideElement SPY_HEART = $$("p").find(exactText("SpyHeart"));
    private static final SelenideElement TARO = $$("p").find(exactText("Таро"));
    private static final SelenideElement MATRIX_CALCULATION = $$("p").find(exactText("Расчет матрицы"));
    private static final SelenideElement MEMORY_TRAINER = $$("p").find(exactText("Тренажер памяти"));
    private static final SelenideElement CHANNEL_ACCOUNTING = $$("p").find(exactText("Учет каналов"));
    private static final SelenideElement PRODUCT_CARD_EDITOR = $$("p").find(exactText("Редактор карточек товара"));

    // Элементы управления каруселью
    private static final SelenideElement BUTTON_NEXT_SLIDE = $("button[aria-label='Следующий слайд']");
    private static final SelenideElement BUTTON_PREVIOUS_SLIDE = $("button[aria-label='Предыдущий слайд']");
    private static final SelenideElement FIRST_DASHBOARD = $$(".tutorial-product-step-4-dash").get(0);

    // Коллекция дашбордов для удобства проверки
    private static final Map<String, SelenideElement> DASHBOARDS = new LinkedHashMap<>() {{
        put("Трекер питания", NUTRITION_TRACKER);
        put("Натальная карта", NATAL_CHART);
        put("SpyHeart", SPY_HEART);
        put("Таро", TARO);
        put("Расчет матрицы", MATRIX_CALCULATION);
        put("Тренажер памяти", MEMORY_TRAINER);
        put("Учет каналов", CHANNEL_ACCOUNTING);
        put("Редактор карточек товара", PRODUCT_CARD_EDITOR);
    }};

    @Step("Проверка видимости дашборда: {dashboardName}")
    private static void verifyDashboardVisibility(String dashboardName, SelenideElement dashboardElement) {
        step("Проверка: " + dashboardName, () -> {
            dashboardElement.shouldBe(visible, Duration.ofSeconds(5))
                    .shouldHave(exactText(dashboardName));

            AllureAttachments.attachText("Дашборд проверен", "✓ " + dashboardName + " - отображается корректно");
            AllureAttachments.attachScreenshot(dashboardName);
        });
    }

    @Step("Проверка что на первом позиции дашбор: {expectedDashboard}")
    private static void verifyFirstDashboard(String expectedDashboard) {
        FIRST_DASHBOARD.shouldBe(visible, Duration.ofSeconds(5));

        //Проверяем текст дашборда
        String actualText = FIRST_DASHBOARD.getText();
        assertTrue(actualText.contains(expectedDashboard),
        String.format("На первой позиции не тот дашборд. Ожидалось: %s, Фактически: %s",
                expectedDashboard, actualText));

        AllureAttachments.attachText("Проверка дашборда",
                "На первой позиции: " + actualText);
        AllureAttachments.attachScreenshot(actualText);

    }

    @Step("Безопасный клик по карусели")
    public static void safeCarouselClick(SelenideElement button) {
        step("Клик по карусели", () -> {
            button.shouldBe(visible, Duration.ofSeconds(5))
                    .shouldBe(interactable, Duration.ofSeconds(3))
                    .click();
            WaitHelper.waitForAnimationComplete();
            WaitHelper.waitFor(Duration.ofSeconds(1)); // Дополнительная стабилизация
        });
    }

    @Step("Проверка видимости и доступности всех дашбордов")
    public static void verifyDashboardsVisibility() {
        step("Прокрутка к разделу 'Приложения'", () -> {
            APPLICATION.scrollTo()
                    .shouldBe(visible, Duration.ofSeconds(10))
                    .shouldHave(exactText("Приложения"));
            AllureAttachments.attachText("Раздел приложений", "Раздел 'Приложения' успешно загружен");
            AllureAttachments.attachScreenshot("Модуль 'Приложения'");
        });

        step("Проверка дашбордов на первом слайде", () -> {
            verifyDashboardVisibility("Трекер питания", NUTRITION_TRACKER);
            verifyDashboardVisibility("Натальная карта", NATAL_CHART);
            verifyDashboardVisibility("SpyHeart", SPY_HEART);
        });

        step("Навигация по карусели дашбордов", () ->
                navigateThroughDashboards());

        step("Проверка навигации назад", () -> {
            BUTTON_PREVIOUS_SLIDE.shouldBe(visible).click();
            WaitHelper.waitForAnimationComplete();
            SPY_HEART.shouldBe(visible);
            AllureAttachments.attachText("Навигация", "Успешная навигация назад по карусели");
        });
    }

    @Step("Навигация по карусели дашбордов")
    private static void navigateThroughDashboards() {
        step("Переход к следующим слайдам", () -> {
            //Слайд 2: Таро
            safeCarouselClick(BUTTON_NEXT_SLIDE);
            verifyDashboardVisibility("Таро", TARO);

            //Слайд 3: Расчёт матрицы
            safeCarouselClick(BUTTON_NEXT_SLIDE);
            verifyDashboardVisibility("Расчет матрицы", MATRIX_CALCULATION);

            //Слайд 4: пропускаем повторный SpyHeart
            safeCarouselClick(BUTTON_NEXT_SLIDE);
            safeCarouselClick(BUTTON_NEXT_SLIDE);

            //Слайд 5: Тренажёр памяти
            verifyDashboardVisibility("Тренажер памяти", MEMORY_TRAINER);

            //Слайд 6: пропускаем повторный SpyHeart
            safeCarouselClick(BUTTON_NEXT_SLIDE);
            safeCarouselClick(BUTTON_NEXT_SLIDE);


            //Слайд 7: Учёт каналов и редактор карточек
            verifyDashboardVisibility("Учет каналов", CHANNEL_ACCOUNTING);
            verifyDashboardVisibility("Редактор карточек товара", PRODUCT_CARD_EDITOR);
        });
    }

    @Step("Получение статистики по дашбордам")
    public static Map<String, Boolean> getDashboardsStatus() {
        Map<String, Boolean> status = new LinkedHashMap<>();

        for (Map.Entry<String, SelenideElement> entry : DASHBOARDS.entrySet()) {
            try {
                boolean isVisible = entry.getValue().isDisplayed();
                status.put(entry.getKey(), isVisible);
            } catch (Exception e) {
                status.put(entry.getKey(), false);
            }
        }
        AllureAttachments.attachText("Статус дашбордов", formatStatusReport(status));
        return status;
    }

    private static String formatStatusReport(Map<String, Boolean> status) {
        StringBuilder report = new StringBuilder("Статус отображения дашбордов:\n");
        for (Map.Entry<String, Boolean> entry : status.entrySet()) {
            report.append(entry.getValue() ? "✅ " : "❌ ")
                    .append(entry.getKey())
                    .append("\n");
        }
        return report.toString();
    }

    @Step("проверка начального состояния карусели")
    private static void checkCarouselInitialState() {
        System.out.println("H2: ");
        $$("h2").forEach(element -> {
            System.out.println(" - " + element.getText());
        });
    }

    @Step("Проверка видимости дашборда на первом слайде")
    public static void verifyDashboardsVisibilityOnFirstSlide() {
        step("Проверка дашбордов на первом слайде", () -> {
            verifyDashboardVisibility("Таро", TARO);
            verifyDashboardVisibility("Трекер питания", NUTRITION_TRACKER);
            verifyDashboardVisibility("Натальная карта", NATAL_CHART);
        });
    }

    @Step("Тестирование продукта")
    public static void testSingleProduct (String productName, String dashboardName) {
        step("Тестирование продукта: " + productName, ()->{
            step("1. Добавляем продукт", () -> {
                Home.walkthroughtAddingACourse(productName);
            });
            step("2. Обновляем страницу", ()->{
                refresh();
                WaitHelper.waitForPageLoad();
            });
            step("3. Проверяем что в карусели на первой позиции дашборд: " + dashboardName, ()->{
                verifyFirstDashboard(dashboardName);
            });
            step("4. Удаляем курс", ()->{
                Home.deleteProduct(productName);
//                AllureAttachments.attachText("Продукт удален", "✅ " + productName);
//                AllureAttachments.attachScreenshot("Главная страница после удаления продукта");
            });
            step("5. Подготовка к следующему продукту", ()->{
                WaitHelper.waitFor(Duration.ofSeconds(1));
            });
        });
    }

}
