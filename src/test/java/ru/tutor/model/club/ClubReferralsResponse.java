package ru.tutor.model.club;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * Ответ GET /user/club/v2/referrals
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ClubReferralsResponse(
        @JsonProperty("items") List<ReferralItem> items,
        @JsonProperty("pagination") Pagination pagination,
        @JsonProperty("summary") Summary summary
) {
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<ReferralItem> safeItems() {
        return items == null ? List.of() : items;
    }

    /**
     * Количество items с заданным subscriptionStatus.code
     */
    public long countByStatusCode(String code) {
        return safeItems().stream()
                .filter(item -> item.hasStatusCode(code))
                .count();
    }

    /**
     * Количество рефералов без подписки (subscriptionStatus.code == null).
     * Такие рефералы входят в pagination.total, но не учитываются ни одним счётчиком summary,
     * поэтому sum(summary) == total - countWithoutSubscription().
     */
    public long countWithoutSubscription() {
        return safeItems().stream()
                .filter(item -> !item.hasSubscription())
                .count();
    }

    public List<String> linkNames() {
        return safeItems().stream()
                .map(ReferralItem::link)
                .filter(Objects::nonNull)
                .map(ReferralLink::name)
                .toList();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ReferralItem(
            @JsonProperty("userId") Long userId,
            @JsonProperty("name") String name,
            @JsonProperty("avatarUrl") String avatarUrl,
            @JsonProperty("level") Integer level,
            @JsonProperty("subscriptionStatus") SubscriptionStatus subscriptionStatus,
            @JsonProperty("subscriptionDate") String subscriptionDate,
            @JsonProperty("incomePerMonth") Integer incomePerMonth,
            @JsonProperty("incomeOneTime") Integer incomeOneTime,
            @JsonProperty("link") ReferralLink link
    ) {
        public boolean hasStatusCode(String code) {
            return subscriptionStatus != null && code.equals(subscriptionStatus.code());
        }

        /**
         * Есть ли подписка. У реферала без подписки code == null, subscriptionDate == null и link == null.
         */
        public boolean hasSubscription() {
            return subscriptionStatus != null && subscriptionStatus.code() != null;
        }

        public LocalDateTime getSubscriptionDateAsLocalDateTime() {
            if (subscriptionDate == null || subscriptionDate.isBlank()) {
                return null;
            }
            try {
                return LocalDateTime.parse(subscriptionDate, DATE_TIME);
            } catch (Exception e) {
                return null;
            }
        }

        public boolean hasValidAvatarUrl() {
            return avatarUrl != null && avatarUrl.startsWith("http");
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SubscriptionStatus(
            @JsonProperty("code") String code,
            @JsonProperty("label") String label,
            @JsonProperty("isActive") Boolean isActive,
            @JsonProperty("stateLabel") String stateLabel
    ) {
        public boolean isActiveSubscription() {
            return Boolean.TRUE.equals(isActive);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ReferralLink(
            @JsonProperty("id") Long id,
            @JsonProperty("name") String name,
            @JsonProperty("url") String url
    ) {
        public boolean hasName() {
            return name != null && !name.isBlank();
        }

        /**
         * Признак некачественной сериализации на бэке: name = "null" (строка) вместо JSON null.
         */
        public boolean isPlaceholderName() {
            return name == null || "null".equals(name);
        }

        public boolean hasUrl() {
            return url != null && url.startsWith("http");
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Pagination(
            @JsonProperty("count") Integer count,
            @JsonProperty("total") Integer total,
            @JsonProperty("per_page") Integer perPage,
            @JsonProperty("page") Integer page,
            @JsonProperty("pages") Integer pages
    ) {
        /**
         * pages = 0 при total == 0, иначе ceil(total / per_page)
         */
        public int expectedPages() {
            int totalValue = Objects.requireNonNullElse(total, 0);
            int perPageValue = Objects.requireNonNullElse(perPage, 0);
            if (totalValue == 0 || perPageValue <= 0) {
                return 0;
            }
            return (int) Math.ceil((double) totalValue / perPageValue);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Summary(
            @JsonProperty("trial") Integer trial,
            @JsonProperty("lite") Integer lite,
            @JsonProperty("premium") Integer premium,
            @JsonProperty("lifetime") Integer lifetime,
            @JsonProperty("paymentError") Integer paymentError,
            @JsonProperty("unsubscribed") Integer unsubscribed
    ) {
        public int total() {
            return sum(trial, lite, premium, lifetime, paymentError, unsubscribed);
        }

        public int byStatusCode(String code) {
            return switch (code) {
                case "trial" -> value(trial);
                case "lite" -> value(lite);
                case "premium" -> value(premium);
                case "lifetime" -> value(lifetime);
                default -> throw new IllegalArgumentException("Неизвестный код статуса: " + code);
            };
        }

        public boolean allNonNegative() {
            return value(trial) >= 0 && value(lite) >= 0 && value(premium) >= 0
                    && value(lifetime) >= 0 && value(paymentError) >= 0 && value(unsubscribed) >= 0;
        }

        public static int value(Integer number) {
            return Objects.requireNonNullElse(number, 0);
        }

        private static int sum(Integer... numbers) {
            int result = 0;
            for (Integer number : numbers) {
                result += value(number);
            }
            return result;
        }
    }
}