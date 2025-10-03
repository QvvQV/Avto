package ru.tutor.utils;

import io.qameta.allure.Allure;
import io.restassured.response.Response;

public class ResponseAnalyzer {

    public static void analyzeAndAttachResponse(Response response, String testName) {
        // Прикрепляем всю информацию о ответе для отладки
        Allure.addAttachment(testName + " - Status Code",
            "text/plain", String.valueOf(response.getStatusCode()));

        Allure.addAttachment(testName + " - Headers",
            "text/plain", response.getHeaders().toString());

        Allure.addAttachment(testName + " - Body",
            "application/json", response.getBody().asString());

        Allure.addAttachment(testName + " - Response Time",
            "text/plain", response.getTime() + "ms");

        // Логируем основные заголовки
        System.out.println("=== Response Analysis ===");
        System.out.println("Status: " + response.getStatusCode());
        System.out.println("Content-Type: " + response.getContentType());
        System.out.println("Headers: " + response.getHeaders());
        System.out.println("Time: " + response.getTime() + "ms");
        System.out.println("========================");
    }
}
