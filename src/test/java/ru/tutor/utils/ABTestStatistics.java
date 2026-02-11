package ru.tutor.utils;

import io.qameta.allure.Allure;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ABTestStatistics {

    private final Map<Integer, AtomicInteger> versionDistribution = new HashMap<>();
    private final Map<Integer, AtomicInteger> successfulTests = new HashMap<>();
    private int totalTests = 0;
    private int successfulTotal = 0;

    public void addTestResult(int version, boolean isSuccessful) {
        versionDistribution.putIfAbsent(version, new AtomicInteger(0));
        versionDistribution.get(version).incrementAndGet();

        successfulTests.putIfAbsent(version, new AtomicInteger(0));
        if (isSuccessful) {
            successfulTests.get(version).incrementAndGet();
            successfulTotal++;
        }

        totalTests++;
        attachStatistics();
    }

    public String getStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== A/B Test Distribution Statistics ===\n");
        stats.append(String.format("Total tests: %d\n", totalTests));
        stats.append(String.format("Successful tests: %d (%.1f%%)\n",
                successfulTotal, totalTests > 0 ? (successfulTotal * 100.0 / totalTests) : 0));

        stats.append("\nDistribution by version:\n");
        for (Map.Entry<Integer, AtomicInteger> entry : versionDistribution.entrySet()) {
            int version = entry.getKey();
            int count = entry.getValue().get();
            int successful = successfulTests.getOrDefault(version, new AtomicInteger(0)).get();

            double percentage = totalTests > 0 ? (count * 100.0 / totalTests) : 0;
            double successRate = count > 0 ? (successful * 100.0 / count) : 0;

            String versionName = version == 1 ? "Version 1 (с иконкой)" : "Version 0 (без иконки)";

            stats.append(String.format("  %s:\n", versionName));
            stats.append(String.format("    Tests: %d (%.1f%%)\n", count, percentage));
            stats.append(String.format("    Successful: %d (%.1f%%)\n", successful, successRate));
        }

        // Расчет распределения
        int version0Count = versionDistribution.getOrDefault(0, new AtomicInteger(0)).get();
        int version1Count = versionDistribution.getOrDefault(1, new AtomicInteger(0)).get();

        if (version0Count + version1Count > 0) {
            double version0Percent = (version0Count * 100.0) / (version0Count + version1Count);
            double version1Percent = (version1Count * 100.0) / (version0Count + version1Count);

            stats.append(String.format("\nActual split: %.1f%% / %.1f%%\n",
                    version0Percent, version1Percent));
            stats.append(String.format("Expected split: 50%% / 50%%\n"));

            double deviation = Math.abs(version0Percent - 50);
            stats.append(String.format("Deviation from 50/50: ±%.1f%%\n", deviation));
        }

        return stats.toString();
    }

    public void verifyDistribution(int expectedA, int expectedB, double tolerancePercent) {
        int version0Count = versionDistribution.getOrDefault(0, new AtomicInteger(0)).get();
        int version1Count = versionDistribution.getOrDefault(1, new AtomicInteger(0)).get();
        int total = version0Count + version1Count;

        if (total > 0) {
            double actualPercentage0 = (version0Count * 100.0) / total;
            double actualPercentage1 = (version1Count * 100.0) / total;

            String distributionInfo = String.format(
                    "Expected distribution: %d%% / %d%%\n" +
                            "Actual distribution: %.1f%% / %.1f%%\n" +
                            "Tolerance: ±%.1f%%",
                    expectedA, expectedB, actualPercentage0, actualPercentage1, tolerancePercent
            );

            Allure.addAttachment("Distribution Verification", "text/plain", distributionInfo);

            assert Math.abs(actualPercentage0 - expectedA) <= tolerancePercent :
                    String.format("Distribution for version 0 out of tolerance. " +
                            "Expected: %d%%, Actual: %.1f%%", expectedA, actualPercentage0);

            assert Math.abs(actualPercentage1 - expectedB) <= tolerancePercent :
                    String.format("Distribution for version 1 out of tolerance. " +
                            "Expected: %d%%, Actual: %.1f%%", expectedB, actualPercentage1);
        }
    }

    private void attachStatistics() {
        Allure.addAttachment("Current Statistics", "text/plain", getStatistics());
    }
}
