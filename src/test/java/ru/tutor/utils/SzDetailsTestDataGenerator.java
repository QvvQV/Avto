package ru.tutor.utils;


import net.datafaker.Faker;
import ru.tutor.model.SzDetailsRequest;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class SzDetailsTestDataGenerator {
    private static final Faker faker = new Faker(new Locale("ru"));
    private static final Random random = new Random();

    /**
     * Генерация валидных тестовых данных для самозанятого
     */
    public static SzDetailsRequest generateValidSzDetails(Integer userId, String email) {
        SzDetailsRequest request = new SzDetailsRequest(email, userId);

        // Генерация данных
        request.setFullName(faker.name().fullName());
        request.setPhone(generateRussianPhone());
        request.setDateBirth("1950-07-27");
        request.setInn(generateInn());
        request.setIdEdo(generateIdEdo());

        // Адрес
        String address = faker.address().fullAddress();
        request.setAddressRegister(address);
        request.setAddressFact(address);
        request.setSameAddress(true);

        // Паспортные данные
        request.setPassSerial(generatePassportSerial());
        request.setPassNumber(generatePassportNumber());
        request.setPassIssued("1958-12-30");
        request.setPassIssuer(faker.company().name() + " ОВД");
        request.setPassDepartment(faker.numerify("###-###"));

        // Банковские реквизиты
        request.setBankName("ПАО " + faker.company().name() + " Банк");
        request.setBankAccount(generateBankAccount());
        request.setBankKorAccount(generateBankAccount());
        request.setBik(generateBik());

        // Флаги
        request.setIsResident(true);

        return request;
    }

    /**
     * Генерация данных для нерезидента
     */
    public static SzDetailsRequest generateNonResidentSzDetails(Integer userId, String email) {
        SzDetailsRequest request = new SzDetailsRequest(email, userId);
        request.setAddressFact(faker.address().fullAddress());
        request.setIsResident(false);
        request.setFullName(faker.name().fullName());
        return request;
    }

    /**
     * Генерация невалидных данных (для негативных тестов)
     */
    public static SzDetailsRequest generateInvalidSzDetails(Integer userId, String email) {
        SzDetailsRequest request = new SzDetailsRequest(email, userId);
        request.setFullName(""); // Пустое имя
        request.setPhone("invalid phone");
        request.setDateBirth("invalid Date");
        request.setInn("123"); // короткий
        request.setIsResident(true);
        return request;
    }

    private static String generateRussianPhone() {
        return faker.phoneNumber().phoneNumber();
    }

    private static String generateInn() {
        StringBuilder inn = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            inn.append(random.nextInt(10));
        }
        return inn.toString();
    }

    private static String generateIdEdo() {
        return faker.regexify("[a-z0-9]{2,4}-[a-z0-9]{2,4}");
    }

    private static String generatePassportSerial() {
        return faker.regexify("[0-9]{4}");
    }

    private static String generatePassportNumber() {
        return faker.regexify("[0-9]{6}");
    }

    private static String generateBankAccount() {
        return faker.regexify("[0-9]{20}");
    }

    private static String generateBik() {
        return faker.regexify("[0-9]{9}");
    }

    /**
     * Конвертация объекта в Map для передачи в API
     */
    public static java.util.Map<String, Object> toMap(SzDetailsRequest request) {
        java.util.Map<String, Object> map = new HashMap<>();

        if (request.getFullName() != null) map.put("addressFact", request.getAddressFact());
        if (request.getAddressRegister()  != null) map.put("addressRegister", request.getAddressRegister());
        if (request.getBankAccount()  != null) map.put("bankAccount", request.getBankAccount());
        if (request.getBankKorAccount() != null) map.put("bankKorAccount", request.getBankKorAccount());
        if (request.getBankName() != null) map.put("bankName", request.getBankName());
        if (request.getBik() != null ) map.put("bik", request.getBik());
        if (request.getDateBirth()  != null) map.put("dateBirth", request.getDateBirth());
        if (request.getEmail()  != null) map.put("email", request.getEmail());
        if (request.getFullName()  != null) map.put("fullName", request.getFullName());
        if (request.getIdEdo()   != null ) map.put("idEdo", request.getIdEdo());
        if (request.getInn() != null) map.put("inn", request.getInn());
        if (request.getIsResident()  != null) map.put("isResident", request.getIsResident());
        if (request.getSameAddress()  != null) map.put("sameAddress", request.getSameAddress());
        if (request.getPassDepartment()  != null) map.put("passDepartment", request.getPassDepartment());
        if (request.getPassIssued()   != null) map.put("passIssued", request.getPassIssued());
        if (request.getPassIssuer()   != null) map.put("passIssuer", request.getPassIssuer());
        if (request.getPassNumber()   != null) map.put("passNumber", request.getPassNumber());
        if (request.getPassSerial()  != null ) map.put("passSerial", request.getPassSerial());
        if (request.getOgrnip() !=null) map.put("ogrnip", request.getOgrnip());
        if (request.getPhone()      != null ) map.put("phone", request.getPhone());
        if (request.getUserId()    != null ) map.put("userId", request.getUserId());

        return map;
    }

    /**
     * Генерация валидных тестовых данных для ИП
     */
    public static SzDetailsRequest generateValidIpDetails(Integer userId, String email) {
        SzDetailsRequest request = new SzDetailsRequest(email, userId);

        // Генерация данных
        request.setFullName(faker.name().fullName());
        request.setPhone(generateRussianPhone());
//        request.setDateBirth("1950-07-27");
        request.setInn(generateInn());
        request.setOgrnip(generateOgrnip());
        request.setIdEdo(generateIdEdo());

        // Адрес
        String address = faker.address().fullAddress();
        request.setAddressRegister(address);
        request.setAddressFact(address);
        request.setSameAddress(true);

//        // Паспортные данные
//        request.setPassSerial(generatePassportSerial());
//        request.setPassNumber(generatePassportNumber());
//        request.setPassIssued("1958-12-30");
//        request.setPassIssuer(faker.company().name() + " ОВД");
//        request.setPassDepartment(faker.numerify("###-###"));

        // Банковские реквизиты
        request.setBankName("ПАО " + faker.company().name() + " Банк");
        request.setBankAccount(generateBankAccount());
        request.setBankKorAccount(generateBankAccount());
        request.setBik(generateBik());

        // Флаги
        request.setIsResident(true);

        return request;
    }

    public static String generateOgrnip() {
        // ОГРНИП: 15 цифр
        return faker.regexify("[0-9]{15}");
    }

    /**
     * Конвертация объекта в Map для передачи в API
     */
}
