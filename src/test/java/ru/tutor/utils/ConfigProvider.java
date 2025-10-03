package ru.tutor.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Properties;

public class ConfigProvider {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigProvider.class.getClassLoader().getResourceAsStream("config.properties")) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить config.properties", e);
        }
    }

    public static String getBaseUrl() {
        return properties.getProperty("base.url");
    }

    public static String getBasicAuth() {
        String login = properties.getProperty("auth.login");
        String password = properties.getProperty("auth.password");
        return Base64.getEncoder().encodeToString((login + ":" + password).getBytes());
    }
}
