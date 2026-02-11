package ru.tutor.test.context;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс для чтения contex по тестам
 */

public class TestExecutionContext {
    private LocalDateTime originalCreatedDateTime;
    private LocalDateTime expiresBeforePayment;
    private final Map<String, String> stateSnapshots = new HashMap<>();
    private String testName;
    private String currentProductId;
    private String paymentMethod;

    public TestExecutionContext() {
    }

    public TestExecutionContext(String testName) {
        this.testName = testName;
    }

    public void setOriginalCreatedDate(LocalDateTime dateTime) {
        this.originalCreatedDateTime = dateTime;
    }

    public LocalDateTime getOriginalCreatedDate() {
        return originalCreatedDateTime;
    }

    public void setExpiresBeforePayment(LocalDateTime dateTime) {
        this.expiresBeforePayment = dateTime;
    }

    public LocalDateTime getExpiresBeforePayment() {
        return expiresBeforePayment;
    }

    public void saveState(String key, String value) {
        stateSnapshots.put(key, value);
    }

    public String getState(String key) {
        return stateSnapshots.get(key);
    }

    public void setCurrentProductId(String productId) {
        this.currentProductId = productId;
    }

    public String getCurrentProductId() {
        return currentProductId;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getTestName() {
        return testName;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void reset() {
        originalCreatedDateTime = null;
        expiresBeforePayment = null;
        stateSnapshots.clear();
        currentProductId = null;
        paymentMethod = null;
    }

    @Override
    public String toString() {
        return String.format(
                "TestExecutionContext{testName='%s', productId='%s', paymentMethod='%s', states=%d}",
                testName, currentProductId, paymentMethod, stateSnapshots.size()
        );
    }

}
