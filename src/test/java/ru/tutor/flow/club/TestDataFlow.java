package ru.tutor.flow.club;

import net.datafaker.Faker;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import ru.tutor.model.club.SzDetailsRequest;
import ru.tutor.utils.ClubTestDataGenerator;

import java.util.HashMap;
import java.util.Map;

public class TestDataFlow {

    private final Faker faker = new Faker();
    private final Integer userId;
    private final String email;

    public TestDataFlow(Integer userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    @Step("Генерация валидных данных самозанятого")
    public SzDetailsRequest generateValidSelfEmployedData() {
        SzDetailsRequest request = ClubTestDataGenerator.generateValidSzDetails(userId, email);
        logGenerateData("Самозанятый", request);
        return request;
    }

    @Step("генерация валидных данных ИП")
    public SzDetailsRequest generateValidIpData() {
        SzDetailsRequest request = ClubTestDataGenerator.generateValidIpDetails(userId, email);
        logGenerateData("ИП", request);
        return request;
    }

    @Step("Генерация минимального набора данных")
    public Map<String, Object> generateMinimalPayload() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId",  userId);
        payload.put("email", email);
        payload.put("fullName", faker.name().fullName());
        payload.put("phone", faker.phoneNumber().phoneNumber());
        payload.put("dateBirth", "1990-01-01");
        payload.put("inn", "123456789012");
        payload.put("isResident", true);

        Allure.addAttachment("Минимальные данные", "text/plain",
                String.format("""
                        userId: %d
                        email: %s
                        fullName: %s
                        phone: %s
                        dateBirth: %s
                        inn: %s
                        isResident: %s""",
                        payload.get("userId"),
                        payload.get("email"),
                        payload.get("fullName"),
                        payload.get("phone"),
                        payload.get("dateBirth"),
                        payload.get("inn"),
                        payload.get("isResident")));

        return payload;
    }

    @Step("Генерация неполных данных ИП")
    public Map<String, Object> generateIncompleteIpPayload() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("email",  email);
        payload.put("fullName", faker.name().fullName());
        payload.put("phone", faker.phoneNumber().phoneNumber());

        Allure.addAttachment("Неполные данные ИП", "text/plain",
                String.format("""
                        email: %s
                        fullName: %s
                        phone: %s
                        (отсутствуют: ИНН, ОГРНИП, дата рождения)""",
                        payload.get("email"),
                        payload.get("fullName"),
                        payload.get("phone")));

        return payload;
    }

    @Step("Генерация данных с невалидным email")
    public SzDetailsRequest generateDataWithInvalidEmail() {
        SzDetailsRequest request = generateValidSelfEmployedData();
        request.setEmail("invalid_email");

        Allure.addAttachment("Невалидный email", "text/plain",
                String.format("Email: %s (невалидный)", request.getEmail()));

        return request;
    }

    @Step("Генерация данных с невалидным ОГРНИП")
    public SzDetailsRequest generateDataWithInvalidOgrnip() {
        SzDetailsRequest request = generateValidIpData();
        request.setOgrnip("invalid_grnip");

        Allure.addAttachment("Невалидный ОГРНИП", "text/plain",
                String.format("ОГРНИП: %s (невалидный)", request.getOgrnip()));

        return request;
    }

    @Step("Генерация данных с неверным serId")
    public SzDetailsRequest generateDataWithWrongUserId(Integer wrongUserId) {
        SzDetailsRequest request = ClubTestDataGenerator.generateValidSzDetails(wrongUserId, email);

        Allure.addAttachment("Неверный userId", "text/plain",
                String.format("userId: %d (должен быть %d)", wrongUserId, userId));

        return request;
    }

    private void logGenerateData (String type, SzDetailsRequest request) {
        Allure.addAttachment(String.format("Сгенерированные данные (%s)", type), "text/plain",
                String.format("""
                        ФИО: %s
                        Телефон: %s
                        ИНН: %s
                        ОГРНИП: %s
                        Дата рождения: %s
                        Email: %s
                        Резидент: %s""",
                        request.getFullName(),
                        request.getPhone(),
                        request.getInn(),
                        request.getOgrnip() != null ? request.getOgrnip() : "не указан",
                        request.getDateBirth(),
                        request.getEmail(),
                        request.getIsResident()));
    }
}
