package ru.tutor.model.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import net.datafaker.Faker;
import ru.tutor.constants.PaymentConstants;

import java.util.Locale;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderRequest {

    private static final Faker faker = new Faker(Locale.ENGLISH);

    @JsonProperty("accountId")
    private String accountId;

    @JsonProperty("amount")
    @JsonSetter(nulls = Nulls.SKIP)
    private Double amount = 0.0;

    @JsonProperty("description")
    private String description;

    @JsonProperty("email")
    private String email;

    @JsonProperty("paymentType")
    @JsonSetter(nulls = Nulls.SKIP)
    private Integer paymentType = 0;

    @JsonProperty("currency")
    @JsonSetter(nulls = Nulls.SKIP)
    private String currency = PaymentConstants.DEFAULT_CURRENCY;

    @JsonProperty("skin")
    @JsonSetter(nulls = Nulls.SKIP)
    private String skin = PaymentConstants.DEFAULT_SKIN;

    @JsonProperty("autoClose")
    @JsonSetter(nulls = Nulls.SKIP)
    private Integer autoClose = PaymentConstants.DEFAULT_AUTO_CLOSE;

    @JsonProperty("language")
    @JsonSetter(nulls = Nulls.SKIP)
    private String language = PaymentConstants.DEFAULT_LANGUAGE;

    @JsonProperty("subscriptionType")
    @JsonSetter(nulls = Nulls.SKIP)
    private String subscriptionType = "LIGHT";

    @JsonProperty("userId")
    private String userId;

    private transient String sessionId;

    // Конструкторы
    public OrderRequest() {
        this.sessionId = UUID.randomUUID().toString();
    }

    public OrderRequest(String accountId, Double amount, String description, String email) {
        this();
        this.accountId = accountId;
        this.amount = amount != null ? amount : 0.0;
        this.description = description;
        this.email = email;
    }

    // Геттеры и сеттеры
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount != null ? amount : 0.0;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        if (email == null || email.trim().isEmpty()) {
//            email = generateUniqueEmail();
            email = faker.internet().emailAddress();
        }
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(Integer paymentType) {
        this.paymentType = paymentType != null ? paymentType : 0;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency != null ? currency : "RUB";
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin != null ? skin : "mini";
    }

    public Integer getAutoClose() {
        return autoClose;
    }

    public void setAutoClose(Integer autoClose) {
        this.autoClose = autoClose != null ? autoClose : 3;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language != null ? language : "ru-RU";
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType != null ? subscriptionType : "LIGHT";
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    // Методы для генерации email (аналог faker.internet.address())
    private String generateUniqueEmail() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomPart = UUID.randomUUID().toString().substring(0, 8);

        String[] domains = {"gmail.com", "yandex.ru", "mail.ru", "outlook.com", "test-order.ru"};
        String domain = domains[(int) (System.currentTimeMillis() % domains.length)];

        return String.format("order_%s_%s@%s",
                randomPart,
                timestamp.substring(timestamp.length() - 6),
                domain);
    }

    public String generateEmailWithUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            username = "user";
        }

        String randomPart = UUID.randomUUID().toString().substring(0, 6);
        String timestamp = String.valueOf(System.nanoTime()).substring(10, 15);

        String[] domains = {"order-test.com", "subscription.ru", "payment.demo", "light-order.org"};
        String domain = domains[(int) (Math.random() * domains.length)];

        return String.format("%s_%s_%s@%s",
                username.toLowerCase().replaceAll("\\s+", "_"),
                randomPart,
                timestamp,
                domain);
    }

    public void refreshEmail() {
        this.email = generateUniqueEmail();
    }

    public boolean isGeneratedEmail() {
        if (email == null) return false;

        return email.contains("@order-test.") ||
                email.contains("@subscription.") ||
                email.contains("order_") ||
                email.startsWith("user_");
    }

    // Валидационные методы
    public boolean isValid() {
        return hasAccountId() && hasValidAmount() && hasDescription() && hasEmail();
    }

    public boolean hasAccountId() {
        return accountId != null && !accountId.trim().isEmpty();
    }

    public boolean hasValidAmount() {
        return amount != null && amount > 0;
    }

    public boolean hasDescription() {
        return description != null && !description.trim().isEmpty();
    }

    public boolean hasEmail() {
        return email != null && !email.trim().isEmpty();
    }

    public boolean isValidEmailFormat() {
        if (email == null || email.length() > 254) return false;
        return email.matches("^[A-Za-z0-9._-]+@[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?(?:\\\\.[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?){1,}\\\\.[A-Za-z]{2,}$");
    }
}