package ru.tutor.utils;

import net.datafaker.Faker;
import java.util.Locale;

public class DataGenerator {
    private static final Faker faker = new Faker(Locale.ENGLISH);

    private DataGenerator() {}

    public static String randomEmail() {
        return faker.internet().emailAddress();
    }

    public static String randomInvalidEmail() {
        return faker.lorem().word() + "@invalid";
    }
}
