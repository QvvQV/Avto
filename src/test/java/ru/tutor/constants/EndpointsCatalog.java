package ru.tutor.constants;

/**
 * Эндпоинты каталога: продукты, курсы, вебинары, квизы, поиск, авторы.
 */
public final class EndpointsCatalog {

    private EndpointsCatalog() {
        throw new IllegalStateException("Utility class");
    }

    // ==================== PRODUCT ENDPOINTS ====================
    /**
     * Список продуктов
     */
    public static final String PRODUCTS_LIST = "/user/products";

    /**
     * Номер продукта
     */
    public static final String PRODUCTS_ID = "/user/products/%s";

    /**
     * Получение информации по модулю
     */
    public static final String PRODUCTS_MODULE = "/user/products/%s/modules/%d";

    /**
     * Завершение модуля (пройдено)
     */
    public static final String PRODUCTS_COMPLETED_MODULE = "/user/orders/%s/completed_module/%d";

    /**
     * Получение списка всех продуктов
     */
    public static final String ALL_PRODUCTS = "/user/products/all";

    /**
     * Список продуктов (app-версия)
     */
    public static final String PRODUCTS_LIST_APP = "/user/products-app";

    /**
     * app-вариант списка всех продуктов
     */
    public static final String ALL_PRODUCTS_APP = "/user/products/all-app";

    /**
     * app-вариант списка премьер
     */
    public static final String PRODUCTS_PREMIERS_APP = "/user/products/premiers-app";

    /**
     * продукт премиум-99
     */
    public static final String PRODUCTS_LIGHT_WEIGHT = "/user/products/light-weight";

    /**
     * продукт лайт-29
     */
    public static final String PRODUCTS_LITE_LIGHTWEIGHT = "/user/products/lite-lightweight";

    /**
     * список мини-курсов
     */
    public static final String MINI_COURSES = "/user/mini-courses";

    /**
     * действие по мини-курсу
     */
    public static final String PRODUCTS_MINI_ID = "/user/products-mini/%s";

    /**
     * список доступных вложений у курса
     */
    public static final String PRODUCTS_ATTACHED_FILES = "/user/products/%s/attached-files";

    /**
     * доступ к конкретному вложению курса
     */
    public static final String PRODUCTS_FILES = "/user/products/%s/files/%s";

    /**
     * Получение информации о курсе
     */
    public static final String USER_COURSES_ID = "/user/courses/%s";

    /**
     * Отзывы курса
     */
    public static final String USER_COURSES_REVIEWS = "/user/courses/%s/reviews";

    /**
     * Рекомендации по курсу
     */
    public static final String USER_COURSES_RECOMMENDATIONS = "/user/courses/%s/recommendations";

    /**
     * список вебинаров пользователя
     */
    public static final String USER_WEBINARS = "/user/%s/webinars";

    /**
     * Данные конкретного вебинара
     */
    public static final String USER_WEBINARS_DETAILS = "/user/%s/webinars/%s/%s";

    /**
     * Чат вебинара
     */
    public static final String USER_WEBINARS_CHAT = "/user/%s/webinars/%s/%s/chat";

    /**
     * b-content
     */
    public static final String PRODUCTS_MODULE_B_CONTENT = "/user/products/%s/module/%d/b-content";

    /**
     * Список квизов продукта
     */
    public static final String USER_PRODUCT_QUIZZES = "/user/%s/product/%s/quizzes";

    /**
     * Детали квиза
     */
    public static final String USER_PRODUCT_QUIZZES_DETAILS = "/user/%s/products/%s/quizzes/%s";

    /**
     * Вопросы квиза
     */
    public static final String USER_PRODUCT_QUIZZES_QUESTIONS = "/user/%s/products/%s/quizzes/%s/questions";

    /**
     * Ранее отправленные ответы по квизу
     */
    public static final String USER_QUIZ_ANSWERS = "/user/%s/quiz_answers/%s";

    /**
     * Отправка ответов на квиз
     */
    public static final String USER_QUIZ_ANSWERS_POST = "/user/%s/quiz_answers";

    /**
     * Получение списка категорий
     */
    public static final String CATEGORIES = "/categories";

    /**
     * Получение списка избранных продуктов
     */
    public static final String USER_FAVORITE_PRODUCTS = "/user/favorite-products";

    /**
     * Добавление/Удаление продукта в избранные
     */
    public static final String USER_FAVORITE_PRODUCTS_ID = "/user/favorite-products/%s";

    /**
     * История поиска
     */
    public static final String SEARCH_HISTORY = "/user/search/history";

    /**
     * Уведомление/заявка по поиску
     */
    public static final String SEARCH_NOTIFY = "/user/search/notify";

    /**
     * удаляет продукт из добавленных по айдишнику заказа
     */
    public static final String PRODUCT_HIDE = "/user/orders/product/%s/hide";

    /**
     * закрепить добавленный курс
     */
    public static final String PRODUCT_PINNED = "/user/orders/pinned/%s/1";

    /**
     * Подписка на уведомление о продукте
     */
    public static final String PRODUCTS_NOTIFY = "/user/products-notify";

    /**
     * Отмена уведомления
     */
    public static final String PRODUCTS_NOTIFY_DELETE = "/user/products-notify/%s";

    // ==================== AUTHORS ENDPOINTS ====================
    /**
     * Информация об авторе
     */
    public static final String USER_AUTHOR_ID = "/user/author/%s";

    /**
     * Активация автора
     */
    public static final String USER_AUTHOR_ACTIVATE = "/user/author/activate";

    /**
     * Общее кол-во курсов в каталоге
     */
    public static final String TOTAL_COURSES = "/total-courses";
}