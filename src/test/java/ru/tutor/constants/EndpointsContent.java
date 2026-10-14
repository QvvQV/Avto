package ru.tutor.constants;

/**
 * Эндпоинты контента: сторис, тутсы, баннеры, видео, сертификаты, главная страница.
 */
public final class EndpointsContent {

    private EndpointsContent() {
        throw new IllegalStateException("Utility class");
    }

    // ==================== Сертификаты ====================
    /**
     * Генерация сертификата (Query: name, orderNumber)
     */
    public static final String CERTIFICATE_GENERATE = "/user/certificate/generate";

    /**
     * Получение сертификата в PDF
     */
    public static final String CERTIFICATE_PDF = "/user/certificate/pdf";

    /**
     * Получение сертификата в PNG
     */
    public static final String CERTIFICATE_PNG = "/user/certificate/png";

    // ==================== VIDEO ENDPOINTS ====================
    /**
     * Список доступных видео в группе "Подкасты"
     */
    public static final String USER_VIDEOS = "/user/videos";

    /**
     * Список Категорий по шоу
     */
    public static final String SHOW_CATEGORIES = "/user/show-categories";

    /**
     * Информация по конкретному видео
     */
    public static final String USER_VIDEO_DETAILS_BY_ID = "/user/videos/%s";

    /**
     * Если просмотр каких-то видео был оборван до завершения модуля
     */
    public static final String VIDEOS_WATCHED_LAST = "/user/videos/watched/last";

    /**
     * Тайминг видео
     */
    public static final String VIDEOS_WATCHED = "/user/videos/watched/create";

    /**
     * Фиксация показа экрана сторис
     */
    public static final String USER_STORIES_SCREEN_POST = "/user/stories/screen";

    /**
     * Фиксация просмотра сторис
     */
    public static final String USER_STORIES_VIEW = "/user/stories/view";

    /**
     * Экран колеса
     */
    public static final String USER_STORIES_SCREEN = "/user/stories-screen";

    // ==================== ENDPOINTS ГЛАВНОЙ СТРАНИЦЫ ====================
    /**
     * Список историй
     */
    public static final String USER_STORIES = "/user/stories";

    /**
     * детали конкретной истории
     */
    public static final String USER_STORIES_ULID = "/user/stories/%s";

    /**
     * детали конкретного баннера
     */
    public static final String USER_BANNERS = "/user/banners/%s";

    /**
     * список тутсов с пагинацией
     */
    public static final String USER_TUTS = "/user/tuts";

    /**
     * Детали конкретного тутса
     */
    public static final String USER_TUTS_DETAILS = "/user/tuts/%s";

    /**
     * Рандомный список категорий тутсов
     */
    public static final String USER_TUTS_RANDOM_LIST = "/user/tuts/random";

    /**
     * Рандомный список всех доступных тутсов
     */
    public static final String TUTS_RANDOM_LIST = "/user/tuts/random-list";

    /**
     * Категории тутсов
     */
    public static final String USER_TUTS_CATEGORIES = "/user/tuts-categories";

    /**
     * Запуск категории тутсов
     */
    public static final String USER_TUTS_CATEGORY_FEED = "/user/tuts/category/%s/feed";

    /**
     * Запись факта просмотра тутса
     */
    public static final String USER_TUTS_VIEW = "/user/tuts/%s/view";

    /**
     * Добавить лайк к тутсу
     */
    public static final String USER_TUTS_LIKES = "/user/tuts/-/likes";

    /**
     * Получить лайки тутса с пагинацией
     */
    public static final String USER_TUTS_LIKES_GET = "/user/tuts/%s/likes/";

    /**
     * Удалить реакции текущего пользователя у тутса
     */
    public static final String USER_TUTS_LIKES_DELETE = "/user/tuts/%s/likes";

    /**
     * Премьеры
     */
    public static final String USER_PRODUCTS_PREMIERS = "/user/products/premiers";

    /**
     * Топ 10 курсов
     */
    public static final String USER_PRODUCT_POSITIONS_TOP10 = "/user/product-positions/top10";

    /**
     * Сейчас смотрят
     */
    public static final String USER_PRODUCTS_WATCHING_NOW = "/user/products/watching/now";

    /**
     * Мини-тренировки
     */
    public static final String MINI_WORKOUTS = "/user/mini-workouts";

    /**
     * Список Шоу
     */
    public static final String USER_SHOWS = "/user/shows";
}