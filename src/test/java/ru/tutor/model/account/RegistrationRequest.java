package ru.tutor.model.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.datafaker.Faker;
import ru.tutor.config.TestConfig;

import java.util.Objects;

public class RegistrationRequest {

    @JsonProperty("email")
    private String email;

    @JsonProperty("name")
    private String name;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("password")
    private String password;

    @JsonProperty("telegram")
    private String telegram;

    @JsonProperty("approvePolicy")
    private Boolean approvePolicy;

    @JsonProperty("gender")
    private Integer gender;

    // ==================== КОНСТРУКТОРЫ ====================

    public RegistrationRequest() {
    }

    public RegistrationRequest(String email, String name, String phone,
                               String password, String telegram, Boolean approvePolicy, Integer gender) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.telegram = telegram;
        this.approvePolicy = approvePolicy;
        this.gender = gender;
    }

    // ==================== ГЕТТЕРЫ ====================

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public String getTelegram() {
        return telegram;
    }

    public Boolean getApprovePolicy() {
        return approvePolicy;
    }

    public Integer getGender() {
        return gender;
    }

    // ==================== СЕТТЕРЫ ====================

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setTelegram(String telegram) {
        this.telegram = telegram;
    }

    public void setApprovePolicy(Boolean approvePolicy) {
        this.approvePolicy = approvePolicy;
    }

    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================

    /**
     * Генерация случайных данных для регистрации
     */

    private static final String TEST_PASSWORD =  TestConfig.getTestPassword();

    public static RegistrationRequest generateRandom() {
        Faker faker = new Faker();

        return new RegistrationRequest(
                faker.internet().emailAddress(),
                faker.name().firstName(),
                faker.phoneNumber().phoneNumber(),
                TEST_PASSWORD,
                "@" + faker.name().firstName(),
                true,
                0
        );
    }

    /**
     * Генерация минимального набора данных (только обязательные поля)
     */
    public static RegistrationRequest generateMinimal() {
        Faker faker = new Faker();

        return new RegistrationRequest(
                faker.internet().emailAddress(),
                faker.name().firstName(),
                "",
                TEST_PASSWORD,
                "",
                true,
                0
        );
    }

    /**
     * Генерация данных без согласия с политикой
     */
    public static RegistrationRequest generateWithoutPolicyApproval() {
        Faker faker = new Faker();

        return new RegistrationRequest(
                faker.internet().emailAddress(),
                faker.name().firstName(),
                faker.phoneNumber().phoneNumber(),
                TEST_PASSWORD,
                "@" + faker.name().firstName(),
                false,
                null

        );
    }

    /**
     * Генерация данных с невалидным email
     */
    public static RegistrationRequest generateWithInvalidEmail() {
        Faker faker = new Faker();

        return new RegistrationRequest(
                "invalid-email",
                faker.name().firstName(),
                faker.phoneNumber().phoneNumber(),
                TEST_PASSWORD,
                "@" + faker.name().firstName(),
                true,
                null
        );
    }

    /**
     * Генерация данных с пустым именем
     */
    public static RegistrationRequest generateWithEmptyName() {
        Faker faker = new Faker();

        return new RegistrationRequest(
                faker.internet().emailAddress(),
                "",
                faker.phoneNumber().phoneNumber(),
                TEST_PASSWORD,
                "@" + faker.name().firstName(),
                true,
                null
        );
    }

    /**
     * Генерация данных с коротким паролем
     */
    public static RegistrationRequest generateWithShortPassword() {
        Faker faker = new Faker();

        return new RegistrationRequest(
                faker.internet().emailAddress(),
                faker.name().firstName(),
                faker.phoneNumber().phoneNumber(),
                "123",
                "@" + faker.name().firstName(),
                true,
                0
        );
    }

    /**
     * Генерация данных с длинным паролем
     */
    public static RegistrationRequest generateWithLongPassword() {
        Faker faker = new Faker();

        return new RegistrationRequest(
                faker.internet().emailAddress(),
                faker.name().firstName(),
                faker.phoneNumber().phoneNumber(),
                "ThisIsAVeryLongPassword1234567890!@#$%",
                "@" + faker.name().firstName(),
                true,
                1
        );
    }

    /**
     * Генерация данных с пустым телефоном
     */
    public static RegistrationRequest generateWithEmptyPhone() {
        Faker faker = new Faker();

        return new RegistrationRequest(
                faker.internet().emailAddress(),
                faker.name().firstName(),
                "",
                TEST_PASSWORD,
                "",
                true,
                null
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegistrationRequest that = (RegistrationRequest) o;
        return Objects.equals(email, that.email) &&
                Objects.equals(name, that.name) &&
                Objects.equals(phone, that.phone) &&
                Objects.equals(password, that.password) &&
                Objects.equals(telegram, that.telegram) &&
                Objects.equals(approvePolicy, that.approvePolicy) &&
                gender == that.gender;
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, name, phone, password, telegram, approvePolicy, gender);
    }

    @Override
    public String toString() {
        return "RegistrationRequest{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", password='[PROTECTED]'" +
                ", telegram='" + telegram + '\'' +
                ", approvePolicy=" + approvePolicy + '\'' +
                ", gender=" + gender +
                '}';
    }
}
