package ru.tutor.utils;

import io.qameta.allure.Allure;

public class AllureAttachments {

    private AllureAttachments() {}

    public static void attachText(String name, String content) {
        Allure.addAttachment(name, "text/plain", content);
    }

    public static void attachJson(String name, String jsonContent) {
        Allure.addAttachment(name, "application/json", jsonContent);
    }
}