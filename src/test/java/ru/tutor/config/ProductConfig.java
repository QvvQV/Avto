package ru.tutor.config;


import java.util.HashMap;
import java.util.Map;

public class ProductConfig {

    private ProductConfig() {}

    // Маппинг product_id -> subdomain и expected product name
    public static final Map<String, ProductDomainInfo> PRODUCT_DOMAINS = new HashMap<>();

    static {
        PRODUCT_DOMAINS.put("cdvg", new ProductDomainInfo("cdvgf", "СДВГ"));
        PRODUCT_DOMAINS.put("burnout", new ProductDomainInfo("burnoutf", "Как бороться с выгоранием"));
        PRODUCT_DOMAINS.put("procrastination", new ProductDomainInfo("procrastinationf", "Прокрастинация. Как с ней работать"));
        PRODUCT_DOMAINS.put("panicatt", new ProductDomainInfo("panicattf", "Панические атаки"));
        PRODUCT_DOMAINS.put("rpp", new ProductDomainInfo("rppf", "РПП"));
        PRODUCT_DOMAINS.put("postdepres", new ProductDomainInfo("postdepresf", "Послеродовая депрессия"));
        PRODUCT_DOMAINS.put("childinj", new ProductDomainInfo("childinjf", "Проработка детских травм"));
        PRODUCT_DOMAINS.put("anxiety", new ProductDomainInfo("anxietyf", "Управление тревожностью"));
        PRODUCT_DOMAINS.put("beadult", new ProductDomainInfo("beadultf", "Как быть взрослым"));
        PRODUCT_DOMAINS.put("stress", new ProductDomainInfo("stressf", "Стрессоустойчивость"));
        PRODUCT_DOMAINS.put("powerpers", new ProductDomainInfo("powerpersf", "Сила убеждения"));
        PRODUCT_DOMAINS.put("gaslighting", new ProductDomainInfo("gaslightingf", "Газлайтинг"));
        PRODUCT_DOMAINS.put("victpot", new ProductDomainInfo("victpotf", "Выход из позиции жертвы"));
        PRODUCT_DOMAINS.put("cogbehych", new ProductDomainInfo("cogbehychf", "Когнитивно-поведенческая психотерапия"));
        PRODUCT_DOMAINS.put("conflict", new ProductDomainInfo("conflictf", "Конфликтология"));
        PRODUCT_DOMAINS.put("cogdistor", new ProductDomainInfo("cogdistorf", "Когнитивные искажения"));
        PRODUCT_DOMAINS.put("angermotions", new ProductDomainInfo("angermotionsf", "Управление гневом и эмоциями"));
        PRODUCT_DOMAINS.put("alcoaddiction", new ProductDomainInfo("alcoaddictionf", "Как помочь близкому побороть алкогольную зависимость"));
        PRODUCT_DOMAINS.put("overweight", new ProductDomainInfo("overweightf", "Психология лишнего веса"));
        PRODUCT_DOMAINS.put("psychosomatics", new ProductDomainInfo("psychosomaticsf", "Женская психосоматика"));
    }

    public static class ProductDomainInfo {
        private final String subdomain;
        private final String expectedProductName;

        public ProductDomainInfo(String subdomain, String expectedProductName) {
            this.subdomain = subdomain;
            this.expectedProductName = expectedProductName;
        }

        public String getSubdomain() { return subdomain; }
        public String getExpectedProductName() { return expectedProductName; }
        public String getApiUrl() {
            return "https://" + subdomain + ".tutorplace.ru/domain";
        }
    }

    public static String getProductApiUrl(String productId) {
        ProductDomainInfo info = PRODUCT_DOMAINS.get(productId);
        return info != null ? info.getApiUrl() : null;
    }

    public static String getExpectedProductName(String productId) {
        ProductDomainInfo info = PRODUCT_DOMAINS.get(productId);
        return info != null ? info.getExpectedProductName() : null;
    }

    public static String getSubdomain(String productId) {
        ProductDomainInfo info = PRODUCT_DOMAINS.get(productId);
        return info != null ? info.getSubdomain() : null;
    }
}