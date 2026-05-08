package ru.tutor.flow.club;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.model.club.SzDetailsRequest;
import ru.tutor.model.club.SzDetailsResponse;
import ru.tutor.steps.club.ClubSzDetailsSteps;

import java.util.Map;

public class ClubDetailsFlow {

    private final ClubSzDetailsSteps szDetailsSteps;
    private final String authToken;

    // Константы статусов
    private static final int STATUS_SUCCESS = 201;
    private static final int STATUS_OK = 200;
    private static final int STATUS_VALIDATION_ERROR = 422;
    private static final int STATUS_BAD_REQUEST = 400;
    private static final int STATUS_UNAUTHORIZED = 401;
    private static final int STATUS_FORBIDDEN = 403;
    private static final int STATUS_NOT_FOUND = 404;

    public ClubDetailsFlow(String authToken, String baseUrl) {
        this.authToken = authToken;
        this.szDetailsSteps = ClubSzDetailsSteps.withBaseUrl(baseUrl);
    }

    @Step("Создание/сохранение данных самозанятого")
    public SaveResult saveSelfEmployedDetails(SzDetailsRequest request) {
        Response response = szDetailsSteps.saveSzDetails(authToken, request);
        return new SaveResult(response);
    }

    @Step("Создание/сохранение данных ИП")
    public SaveResult saveIpDetails(SzDetailsRequest request) {
        Response response = szDetailsSteps.saveIpDetails(authToken, request);
        return new SaveResult(response);
    }

    @Step("Создание/сохранение данных с минимальным набором полей")
    public SaveResult saveWithMinimalFields(Map<String, Object> payload, String type) {
        Response response;
        if ("ip".equalsIgnoreCase(type)) {
            response = szDetailsSteps.saveIpDetails(authToken, payload);
        } else {
            response = szDetailsSteps.saveSzDetails(authToken, payload);
        }
        return new SaveResult(response);
    }

    @Step("Удаление данных клуба")
    public DeleteResult deleteClubDetails() {
        Response response = szDetailsSteps.deleteClubDetails(authToken);
        return new DeleteResult(response);
    }

    @Step("Множественное удаление (проверка идемпотентности)")
    public IdempotenceResult testDeleteIdempotence(int attempts) {
        IdempotenceResult result = new IdempotenceResult();

        for (int i = 1; i <= attempts; i++) {
            Response response = szDetailsSteps.deleteClubDetails(authToken);
            result.addAttempt(i, response.getStatusCode());

            Allure.addAttachment(String.format("Удаление #%d", i), "text/plain",
                    String.format("Статус: %d", response.getStatusCode()));
        }
        return result;
    }

    @Step("Полный цикл: создание → валидация → удаление")
    public LifecycleResult executeFullLifecycle(SzDetailsRequest request, String type) {
        LifecycleResult result = new LifecycleResult();

        // 1. Создание
        if ("ip".equalsIgnoreCase(type)) {
            result.setCreateResult(saveIpDetails(request));
        } else {
            result.setCreateResult(saveSelfEmployedDetails(request));
        }

        // 2. Валидация структуры ответа
        result.setStructureValid(validateCreateResponseStructure(result.getCreateResult().getResponse()));

        // 3. Удаление
        result.setDeleteResult(deleteClubDetails());

        // 4. Проверка удаления
        result.setDeletionValid(validateDeletionResponse(result.getDeleteResult()));

        return result;
    }

    private boolean validateCreateResponseStructure(Response response) {
        try{
            int statusCode = response.getStatusCode();
            String contentType = response.getContentType();
            String corsOrigin = response.getHeader("Access-Control-Allow-Origin");
            String corsCredentials = response.getHeader("Access-Control-Allow-Credentials");

            boolean isValid = statusCode == STATUS_SUCCESS &&
                    contentType !=null &&
                    corsOrigin  != null &&
                    corsCredentials != null;

            Allure.addAttachment("Валидация структуры ответа", "text/plain",
                    String.format("""
                            Статус: %d %s
                            Content-Type: %s
                            CORS Origin: %s
                            CORS Credentials: %s
                            Результат: %s""",
                            statusCode,
                            statusCode == STATUS_SUCCESS ? "✓" : "✗",
                            contentType,
                            corsOrigin,
                            corsCredentials,
                            isValid ? "✅ Валидно" : "❌ Невалидно"));

            return isValid;
        } catch (Exception e) {
            Allure.addAttachment("Ошибка валидации ответа", "text/plain",
                    "Исключение при валидации: " + e.getMessage());
            return false;
        }
    }

    private boolean validateDeletionResponse(DeleteResult deleteResult) {
        int statusCode = deleteResult.getStatusCode();
        boolean isValid = statusCode == STATUS_OK || statusCode == STATUS_SUCCESS;

        if (statusCode == STATUS_OK) {
            String responseBody = deleteResult.getResponseBody();
            isValid = isValid && ("[]".equals(responseBody) || responseBody.isEmpty());
        }

        Allure.addAttachment("Валидация удаления", "text/plain",
                String.format("""
                        Статус: %d
                        Результат: %s""",
                        statusCode,
                        isValid ? "✅ Успешно" : "❌ Ошибка"));

        return isValid;
    }

    // ==================== ВНУТРЕННИЕ КЛАССЫ ====================

    public static class SaveResult {
        private final Response response;
        private final SzDetailsResponse parsedResponse;

        public SaveResult(Response response) {
            this.response = response;
            this.parsedResponse = parseResponse(response);
        }

        private SzDetailsResponse parseResponse(Response response) {
            try {
                if (response.getStatusCode() == STATUS_SUCCESS) {
                    return response.as(SzDetailsResponse.class);
                }
            } catch (Exception e) {
                Allure.addAttachment("Ошибка парсинга ответа", "text/plain",
                        "Не удалось распарсить ответ: " + e.getMessage());
            }
            return null;
        }

        public boolean isSuccessful() {
            return response.getStatusCode() == STATUS_SUCCESS;
        }

        public boolean isValidationError() {
            int code = response.getStatusCode();
            return code == STATUS_VALIDATION_ERROR || code == STATUS_BAD_REQUEST;
        }

        public boolean isAuthError() {
            int code = response.getStatusCode();
            return code == STATUS_UNAUTHORIZED || code == STATUS_FORBIDDEN;
        }

        public int getStatusCode() { return response.getStatusCode(); }
        public Response getResponse() { return response; }
        public SzDetailsResponse getParsedResponse() { return parsedResponse; }
        public String getMessage() {
            return parsedResponse != null ? parsedResponse.getMessage() : "";
        }

        public void log() {
            Allure.addAttachment("Результат сохранения", "text/plain",
                    String.format(
                            """
                                    Статус: %d
                                    Успешно: %s
                                    Сообщение: %s""",
                            getStatusCode(),
                            isSuccessful(),
                            getMessage()));
        }
        }

        public static class DeleteResult {
        private final Response response;

        public DeleteResult(Response response) {
            this.response = response;
        }

        public boolean isSuccessful() {
            int code = response.getStatusCode();
            return code == STATUS_SUCCESS || code == STATUS_OK;
        }

        public boolean isNotFound() {
            return response.getStatusCode() == STATUS_NOT_FOUND;
        }

            public boolean isAuthError() {
                int code = response.getStatusCode();
                return code == STATUS_UNAUTHORIZED || code == STATUS_FORBIDDEN;
            }

        public int getStatusCode() { return response.getStatusCode(); }
            public Response getResponse() { return response; }
            public String getResponseBody() {return response.getBody().asString();}

            public void log() {
            Allure.addAttachment("Результат удаления", "text/plain",
                    String.format("""
                            Статус: %d
                            Успешно: %s
                            Тело: %s
                            """,
                            getStatusCode(),
                            isSuccessful(),
                            getResponseBody()));
            }
    }

    public static class IdempotenceResult {
        private final java.util.List<Attempt> attempts = new java.util.ArrayList<>();

        public void addAttempt(int number, int statusCode) {
            attempts.add(new Attempt(number, statusCode));
        }

        public boolean isIdempotent() {
            if (attempts.isEmpty()) return true;

            int firstStatus = attempts.get(0).statusCode;
            boolean firstSuccess = firstStatus == STATUS_OK || firstStatus == STATUS_SUCCESS;

            for (int i = 1; i < attempts.size(); i++) {
                int status = attempts.get(i).statusCode;
                if (firstSuccess) {
                    // Если первый успешен, остальные должны быть 200/201 или 400
                    if (!(status == STATUS_OK || status == STATUS_SUCCESS || status == STATUS_BAD_REQUEST)) {
                        return false;
                    }
                } else {
                    // Если первый не успешен, все должны быть одинаковыми
                    if (status != firstStatus) {
                        return false;
                    }
                }
            }
            return true;
        }

        public void log() {
            StringBuilder report = new StringBuilder();
            report.append("📊 ПРОВЕРКА ИДЕМПОТЕНТНОСТИ DELETE\n");
            report.append("=".repeat(50)).append("\n");

            for (Attempt attempt : attempts) {
                String statusText;
                if (attempt.statusCode == STATUS_OK) statusText = "✅ Успешно (200)";
                else if (attempt.statusCode == STATUS_SUCCESS) statusText = "✅ Успешно (201)";
                else if (attempt.statusCode == STATUS_NOT_FOUND) statusText = "ℹ Уже удалено (404)";
                else statusText = "❌ Ошибка (" + attempt.statusCode + ")";

                report.append(String.format("Запрос #%d: %s\n", attempt.number, statusText));
            }

            report.append("\n").append("=".repeat(50)).append("\n");
            report.append("Идемпотентность: ").append(isIdempotent() ? "✅ Подтверждена" : "❌ Нарушена");

            Allure.addAttachment("Отчет об идемпотентности", "text/plain", report.toString());
        }

        private static class Attempt {
        final int number;
        final int statusCode;

        Attempt(int number, int statusCode) {
        this.number = number;
        this.statusCode = statusCode;
        }
        }
    }

    public static class LifecycleResult {
        private SaveResult createResult;
        private DeleteResult deleteResult;
        private boolean structureValid;
        private boolean deletionValid;

        public boolean isSuccess() {
            return createResult != null && createResult.isSuccessful()
                    && structureValid
                    && deleteResult != null && deletionValid;
        }

        public void log() {
            Allure.addAttachment("📋 ОТЧЕТ О ЖИЗНЕННОМ ЦИКЛЕ", "text/plain",
                    String.format("""
                            
                            ПОЛНЫЙ ЦИКЛ: СОЗДАНИЕ → УДАЛЕНИЕ
                            
                            1. Создание данных:      %s
                            2. Структура ответа:     %s
                            3. Удаление данных:      %s
                            4. Подтверждение:        %s
                            
                            ИТОГ:                    %s
                            """,
                            createResult != null && createResult.isSuccessful() ? "✅" : "❌",
                            structureValid ? "✅" : "❌",
                            deleteResult != null && deleteResult.isSuccessful() ? "✅" : "❌",
                            deletionValid ? "✅" : "❌",
                            isSuccess() ? "✅ УСПЕШНО" : "❌ ПРОВАЛЕН"));
        }

        // Геттеры и сеттеры
        public void setCreateResult (SaveResult createResult) { this.createResult = createResult; }
        public void setDeleteResult (DeleteResult deleteResult) { this.deleteResult = deleteResult; }
        public void setStructureValid (boolean structureValid) { this.structureValid = structureValid; }
        public void setDeletionValid (boolean deletionValid) { this.deletionValid = deletionValid; }

        public SaveResult getCreateResult() { return createResult; }
        public DeleteResult getDeleteResult() { return deleteResult; }
        public boolean isStructureValid() { return structureValid; }
        public boolean isDeletionValid() { return deletionValid; }
    }
}

