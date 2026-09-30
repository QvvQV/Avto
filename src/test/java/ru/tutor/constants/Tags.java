package ru.tutor.constants;

/**
 * Контролируемый словарь тегов JUnit (ось "Уровень" + ось "Контекст" + ось "Направление").
 * <p>
 * Оси ортогональны, на тест — максимум один тег каждой оси:
 * <ul>
 *   <li>Уровень: E2E, INTEGRATION, SMOKE</li>
 *   <li>Контекст: UI, API, UI_API</li>
 *   <li>Направление: POSITIVE, NEGATIVE</li>
 * </ul>
 */
public final class Tags {
    private Tags() {
    }

    // ==================== Ось 1: Уровень теста ====================

    /** Сквозной сценарий через реальный интерфейс/платежи */
    public static final String E2E = "e2e";

    /** Взаимодействие модулей/ручек без браузера */
    public static final String INTEGRATION = "integration";

    /** Быстрая дымовая проверка ключевого пути */
    public static final String SMOKE = "smoke";

    // ==================== Ось 2: Контекст ====================

    /** Только браузер */
    public static final String UI = "ui";

    /** Чистый API-контракт (без браузера) */
    public static final String API = "api";

    /** Браузер + API-подготовка/проверка */
    public static final String UI_API = "ui_api";

    // ==================== Ось 3: Направление результата ====================

    /** Позитивный сценарий */
    public static final String POSITIVE = "positive";

    /** Негативный сценарий */
    public static final String NEGATIVE = "negative";
}