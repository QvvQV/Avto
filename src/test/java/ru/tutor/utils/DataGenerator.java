package ru.tutor.utils;

import net.datafaker.Faker;
import java.util.Locale;

public class DataGenerator {
    private static final Faker faker = new Faker(new Locale("en"));

    private DataGenerator() {}

    public static String randomEmail() {
        return faker.internet().emailAddress();
    }

    public static String randomPassword() {
        return faker.internet().password(8, 16, true, true, true);
    }

    public static String randomInvalidEmail() {
        return faker.lorem().word() + "@invalid";
    }
}
