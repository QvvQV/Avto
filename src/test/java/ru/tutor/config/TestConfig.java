package ru.tutor.config;

import java.io.InputStream;
import java.util.Properties;

public class TestConfig {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = TestConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (Exception ex) {
            System.out.println("No config.properties found, using defaults");
        }
    }

    public static String getApiBaseUrl() {
        return properties.getProperty("api.base.url", "https://api.dev.tutorplace.ru");
    }

    public static String getClientBaseUrl() {
        return properties.getProperty("client.base.url", "https://client.ttplace.ru");
    }

    public static String getAdminBaseUrl() {
        return properties.getProperty("admin.base.url", "https://admin.ttplace.ru");
    }

    public static String getTestEmail() {
        return properties.getProperty("test.email", "tp5052_2@test.ru");
    }

    public static String getTestPassword() {
        return properties.getProperty("test.password", "12345678");
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(properties.getProperty("headless", "true"));
    }
}
