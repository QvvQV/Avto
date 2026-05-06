package ru.tutor.page.personalAccount;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;
import ru.tutor.utils.UniversalMethod;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Каталог Личного кабинета
public class Catalog {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(15);

    // ==================== КОНСТАНТЫ ====================

    private static final String CATALOG_TEXT = "Каталог";
    private static final String CATALOG_PAGE_TITLE = "Каталог курсов";

    // Классы и атрибуты
    private final String CATEGORY_CONTAINER_CLASS = "._CategoryFilter_dijr2_1";
    private final String CATEGORY_ITEM_CLASS = "._category_1cihl_13";
    private final String CATEGORY_NANE_CLASS = "._name_1cihl_1";
    private final String CATEGORY_IMAGE_CLASS = "img._categoryImg_1cihl_39";
    private final String SCROLL_CONTAINER_CLASS = "._scrollContainer_dijr2_13";
    private final String NAV_BUTTON_CLASS = "._navButton_dijr2_49";
    private final String CATEGORY_PRODUCTS = "._count_121uj_200";

    // URL паттерны
    private final String CATALOG_URL = "/user/catalog";
    private final String CATEGORY_URL_PATTERN = "/user/catalog?catalog-category=";

    // ==================== ЛОКАТОРЫ ====================
    private final SelenideElement CATALOG_LINK = $x("//div[text()='Каталог']");
    private final SelenideElement CATALOG_PAGE_HEADER = $$("h2").find(exactText(CATALOG_PAGE_TITLE));
    private final SelenideElement CATALOG_PAGE_BUTTON = $$("span").find(exactText(CATALOG_TEXT));
    private final SelenideElement CATEGORY_FILTER_CONTAINER = $(CATEGORY_CONTAINER_CLASS);
    public final SelenideElement SCROLL_CONTAINER = $(SCROLL_CONTAINER_CLASS);
    private final ElementsCollection ALL_CATEGORIES = $$(CATEGORY_ITEM_CLASS);
    private final SelenideElement NAV_BUTTON = $(NAV_BUTTON_CLASS);
    private final SelenideElement ALL_CARD_CATEGORY_PRODUCTS = $(CATEGORY_PRODUCTS);

    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================

    /**
     * Получить элемент категории по названию
     */
    public SelenideElement getCategoryByName(String categoryName) {
        return ALL_CATEGORIES.findBy(com.codeborne.selenide.Condition.text(categoryName));
    }

    /**
     * Получить элемент названия категории
     */
    public SelenideElement getCategoryNameElement(SelenideElement category) {
        return category.$(CATEGORY_NANE_CLASS);
    }

    /**
     * Получить элемент изображения категории
     */
    public SelenideElement getCategoryImageElement(SelenideElement category) {
        return category.$(CATEGORY_IMAGE_CLASS);
    }

    /**
     * Получить ссылку категории
     */
    public String getCategoryHref(SelenideElement category) {
        return category.getAttribute("href");
    }

    // ==================== НАВИГАЦИЯ И ПРОВЕРКИ КАТАЛОГА ====================

    @Step("Проверка доступности каталога")
    public void verifyCatalogPresence() {
        step("Валидация элемента каталога", () -> {
            CATALOG_LINK.shouldBe(visible, DEFAULT_TIMEOUT)
                    .shouldBe(interactable)
                    .shouldHave(exactText(CATALOG_TEXT));

            AllureAttachments.attachText(CATALOG_TEXT, "Элемент каталога доступен и кликабелен");
        });
    }

    @Step("Проверка заголовка каталога")
    public void verifyCatalogHeader() {
        step("Валидация текста каталога", () -> {
            String actualText = CATALOG_LINK.getText().trim();

            assertEquals(CATALOG_TEXT, actualText,
                    String.format("Текст каталога не совпадает. Ожидалось: '%s', Фактически: '%s'",
                            CATALOG_TEXT, actualText));

            AllureAttachments.attachText("Проверка каталога",
                    "✓ Текст каталога корректен: " + actualText);
        });
    }

    @Step("Проверка загрузки страницы каталога")
    private void verifyCatalogPageLoaded() {
        step("Валидация страницы каталога", () -> {
            CATALOG_PAGE_HEADER.shouldBe(visible, DEFAULT_TIMEOUT);

            AllureAttachments.attachText("Загрузка каталога", "Страница каталога успешно загружена");
            AllureAttachments.attachScreenshot("Страница каталога");
        });
    }

    @Step("Клик по кнопке 'Каталог'")
    public void clickCatalogButton() {
        UniversalMethod.safeClick(CATALOG_PAGE_BUTTON, CATALOG_TEXT);
    }

    @Step("Проверка наличия категорий")
    public void verifyCatalogHasCategory() {
        clickCatalogButton();
        WaitHelper.waitForPageLoad();
        verifyCategoriesBlockVisible();
    }

    // ==================== ПРОВЕРКИ КАТЕГОРИЙ ====================

    @Step("Проверить, что блок категорий отображается")
    public Catalog verifyCategoriesBlockVisible() {
        CATEGORY_FILTER_CONTAINER.shouldBe(visible, DEFAULT_TIMEOUT);
        return new Catalog();
    }

    @Step("Получить кол-во категорий")
    public int getCategoriesCount() {
        return ALL_CATEGORIES.size();
    }

    @Step("Получить все названия категорий")
    public List<String> getAllCategoryNames() {
        return ALL_CATEGORIES.stream()
                .map(category -> getCategoryNameElement(category).getText())
                .collect(Collectors.toList());
    }

    @Step("Проверить, что все категории отображаются")
    public Catalog verifyAllCategoriesDisplayed() {
        for (SelenideElement category : ALL_CATEGORIES) {
            category.shouldBe(visible, DEFAULT_TIMEOUT);
        }
        AllureAttachments.attachText("Проверка категорий",
                String.format("✅ Все %d категории отображаются", ALL_CATEGORIES.size()));
        return this;
    }

    @Step("Проверить, что категории '{categoryName}' отображается")
    public Catalog verifyCategoryDisplayed(String categoryName) {
        getCategoryByName(categoryName)
                .shouldBe(visible, DEFAULT_TIMEOUT);
        return this;
    }

    @Step("Проверить текст категории 'expectedName'")
    public Catalog verifyCategoryText(String expectedName) {
        SelenideElement category = getCategoryByName(expectedName);
        String actualText = getCategoryNameElement(category).getText();

        assertEquals(expectedName, actualText,
                String.format("Текст категории не совпадает. Ожидалось: %s, Фактически: %s",
                        expectedName, actualText));

        return this;
    }

    @Step("Проверка уникальности названий категорий")
    public Catalog nameUnique() {
        step("Проверить, что нет дублирующихся названий категорий", () -> {
            List<String> allCategoryNames = getAllCategoryNames();
            Set<String> uniqueNames = new HashSet<>(allCategoryNames);

            assertEquals(allCategoryNames.size(), uniqueNames.size(),
                    "Найдены дублирующиеся названия категорий");

            if (allCategoryNames.size() != uniqueNames.size()) {
                Map<String, Long> duplicates = allCategoryNames.stream()
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                        .entrySet().stream()
                        .filter(e -> e.getValue() > 1)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                AllureAttachments.attachText("Дубликаты", duplicates.toString());
            }

            AllureAttachments.attachText("Результат",
                    String.format("✅ Все %d названий уникальны", uniqueNames.size()));
        });
        return this;
    }

    // ==================== ПРОВЕРКИ ССЫЛОК ====================

    @Step("Проверить ссылки всех категорий")
    public Catalog verifyAllCategoryLinks() {
        int checkedCount = 0;

        for (SelenideElement category : ALL_CATEGORIES) {
            String categoryName = getCategoryNameElement(category).getText();
            String href = getCategoryHref(category);

            assert href != null : "Ссылка категории '" + categoryName + "' отсутствует";
            assert href.contains(CATEGORY_URL_PATTERN) :
                    "Неверный формат ссылки для категории: " + categoryName;
            AllureAttachments.attachText("Ссылка", categoryName + " -> " + href);
            checkedCount++;
        }

        AllureAttachments.attachText("Проверка ссылок",
                String.format("✅ Проверено %d ссылок", checkedCount));
        return this;
    }

    // ==================== Проверка иконок ====================

    @Step("Проверить наличие иконок у всех категорий")
    public Catalog verifyAllCategoryIcons() {
        int checkedCount = 0;
        List<String> missingImages = new ArrayList<>();

        for (SelenideElement category : ALL_CATEGORIES) {
            String categoryName = getCategoryNameElement(category).getText();
            SelenideElement img = getCategoryImageElement(category);

            if (!img.exists()) {
                missingImages.add(categoryName + " (элемент img отсутвует)");
                continue;
            }

            img.shouldBe(visible, DEFAULT_TIMEOUT);
            String imgSrc = img.getAttribute("src");
            assert imgSrc != null : "URL иконки отсутствует: " + categoryName;
            checkedCount++;
        }

        AllureAttachments.attachText("Проверка иконок",
                String.format("✅ Проверено %d иконок", checkedCount));
        return this;
    }

    // ==================== НАВИГАЦИЯ ====================

    @Step("Перейти по категории '{categoryName}'")
    public Catalog clickCategory(String categoryName) {
        UniversalMethod.safeClick(getCategoryByName(categoryName), categoryName);
        WaitHelper.waitForPageLoad();
        return this;
    }

    @Step("Проверить URL после перехода в категорию '{categoryName}'")
    public Catalog verifyCategoryUrl(String expectedUrlPattern) {
        String currentUrl = WebDriverRunner.getWebDriver().getCurrentUrl();
        assert currentUrl.contains(expectedUrlPattern) :
                String.format("URL не соответствует ожидаемому. Ожидалось: %s, Фактически: %s",
                        expectedUrlPattern, currentUrl);
        return this;
    }

    @Step("Проверить, что находимся на странице каталога")
    public Catalog verifyOnCatalogPage() {
        String currentUrl = WebDriverRunner.getWebDriver().getCurrentUrl();
        assert currentUrl.contains(CATALOG_URL) :
                String.format("Текущая страница не является каталогом. URL: %s", currentUrl);
        return this;
    }

    // ==================== СКРОЛЛИНГ ====================

    @Step("Получить позицию скролла")
    public long getScrollPosition() {
        return (Long) executeJavaScript(
                "return arguments[0].scrollLeft", SCROLL_CONTAINER);
    }

    @Step("Скроллить вправо на {pixels} пикселей")
    public Catalog scrollRight(int pixels) {
        long initialPosition = getScrollPosition();
        executeJavaScript("arguments[0].scrollLeft += " + pixels, SCROLL_CONTAINER);
        Duration.ofMillis(1800);

        AllureAttachments.attachText("Скроллинг",
                String.format("Начальная позиция: %d, Конечная: %d, Сдвиг: %d",
                        initialPosition, getScrollPosition(), pixels));
        return this;
    }

    @Step("Скроллить влево на {pixels} пикселей")
    public Catalog scrollLeft(int pixels) {
        return scrollRight(-pixels);
    }

    @Step("Проверить, что скроллинг сработал")
    public Catalog verifyScrollChanged(long initialPosition) {
        long currentPosition = getScrollPosition();
        assert currentPosition > initialPosition : "Скроллинг не сработал";
        return this;
    }

    @Step("Проверить, что все категории отображаются после скролла")
    public Catalog verifyAllCategoriesVisibleAfterScroll() {
        for (SelenideElement category : ALL_CATEGORIES) {
            category.shouldBe(visible, DEFAULT_TIMEOUT);
        }
        return this;
    }

    // ==================== ПРОИЗВОДИТЕЛЬНОСТЬ ====================

    @Step("Измерить время загрузки категорий")
    public long measureLoadTime() {
        long startTime = System.currentTimeMillis();
        CATEGORY_FILTER_CONTAINER.shouldBe(visible, DEFAULT_TIMEOUT);
        long loadTime = System.currentTimeMillis() - startTime;

        String performanceLevel = getPerformanceLevel(loadTime);
        AllureAttachments.attachText("Производительность",
                String.format("""
                        📊 Время загрузки: %d ms
                        🎯 Оценка: %s
                        📏 Порог: %d ms
                        """, loadTime, performanceLevel, DEFAULT_TIMEOUT.toMillis()));

        return loadTime;
    }

    private String getPerformanceLevel(long loadTime) {
        if (loadTime < 2000) return "🚀 Отлично";
        if (loadTime < 5000) return "✅ Хорошо";
        if (loadTime < 10000) return "⚠️ Удовлетворительно";
        return "🐌 Медленно";
    }

    // ==================== НАВИГАЦИОННЫЕ КНОПКИ ====================

    @Step("Проверить наличие навигационной кнопки")
    public boolean hasNavigationButton() {
        return NAV_BUTTON.exists() && NAV_BUTTON.isDisplayed();
    }

    @Step("Кликнуть по навигационной кнопке")
    public Catalog clickNavigateButton() {
        if (hasNavigationButton()) {
            UniversalMethod.safeClick(NAV_BUTTON, "Навигационная кнопка");
            Duration.ofMillis(1800);
        }
        return this;
    }

    // ==================== Комбинированные действия ====================

    @Step("Полный цикл: выбрать категорию -> проверить URL")
    public Catalog selectCategoryAndVerifyUrl(String categoryName, String expectedUrl) {
        return clickCategory(categoryName)
                .verifyCategoryUrl(expectedUrl);
    }

    @Step("Проверить, что во всех категориях есть курсы")
    public Catalog verifyAllCategoriesHaveCourses() {
        step("Проверить, что во всех категориях есть курсы", () -> {
            List<String> allCategoryNames = getAllCategoryNames();
            Map<String, Integer> coursesCount = new LinkedHashMap<>();
            List<String> emptyCategories = new ArrayList<>();
            List<String> lowCoursesCategories = new ArrayList<>();

            int totalCourses = 0;
            int processed = 0;

            for (String categoryName : allCategoryNames) {
                // Переходим в категорию
                clickCategory(categoryName);
                WaitHelper.waitForPageLoad();

                // ✅ Используем счетчик курсов
                int count = getCourseCount();
                coursesCount.put(categoryName, count);
                totalCourses += count;

                if (count == 0) {
                    emptyCategories.add(categoryName);
                } else if (count < 3) {
                    lowCoursesCategories.add(String.format("%s (%d %s)",
                            categoryName, count, getCourseWord(count)));
                }

                processed++;

                // Логируем прогресс
                if (processed % 5 == 0 || processed == allCategoryNames.size()) {
                    AllureAttachments.attachText("Прогресс",
                            String.format("Проверено %d из %d категорий\nНайдено курсов: %d",
                                    processed, allCategoryNames.size(), totalCourses));
                }
            }

            // Формируем детальный отчет
            generateCourseStatisticsReport(coursesCount, emptyCategories, lowCoursesCategories, totalCourses);

            // Проверки
            assertTrue(emptyCategories.isEmpty(),
                    String.format("❌ Найдены пустые категории:\n%s",
                            String.join("\n", emptyCategories)));
        });

        return this;
    }

    /**
     * Получить количество курсов в текущей категории
     */
    private int getCourseCount() {
        SelenideElement courseCountElement = $(ALL_CARD_CATEGORY_PRODUCTS);

        if (courseCountElement.exists() && courseCountElement.isDisplayed()) {
            String countText = courseCountElement.getText();
            return extractNumberFromCountText(countText);
        }

        return 0;
    }

    /**
     * Генерирует подробный отчет по категориям
     */
    private void generateCourseStatisticsReport(Map<String, Integer> coursesCount,
                                                List<String> emptyCategories,
                                                List<String> lowCoursesCategories,
                                                int totalCourses) {
        StringBuilder report = new StringBuilder();
        report.append("📊 СТАТИСТИКА ПО КАТЕГОРИЯМ\n");
        report.append("=========================\n\n");

        // Категории с курсами
        long categoriesWithCourses = coursesCount.values().stream()
                .filter(c -> c > 0)
                .count();

        report.append(String.format("\n📈 ОБЩАЯ СТАТИСТИКА:\n"));
        report.append(String.format("  • Всего категорий: %d\n", coursesCount.size()));
        report.append(String.format("  • Категорий с курсами: %d (%.1f%%)\n",
                categoriesWithCourses, (categoriesWithCourses * 100.0) / coursesCount.size()));
        report.append(String.format("  • Всего курсов: %d\n", totalCourses));

        // Пустые категории
        if (!emptyCategories.isEmpty()) {
            report.append("\n❌ ПУСТЫЕ КАТЕГОРИИ (0 курсов):\n");
            emptyCategories.forEach(c -> report.append(String.format("  • %s\n", c)));
        }

        AllureAttachments.attachText("Полный отчет по категориям", report.toString());
    }

    /**
     * Возвращает правильное склонение слова "курс"
     */
    private String getCourseWord(int count) {
        if (count % 10 == 1 && count % 100 != 11) return "курс";
        if (count % 10 >= 2 && count % 10 <= 4 && (count % 100 < 10 || count % 100 >= 20)) return "курса";
        return "курсов";
    }

    /**
     * Извлекает число из текста счетчика
     * Примеры: "21 курс" -> 21, "5 курсов" -> 5, "1 курс" -> 1
     */
    private int extractNumberFromCountText(String text) {
        if (text == null || text.isEmpty()) return 0;

        // Ищем первое число в строке
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }

        return 0;
    }
}