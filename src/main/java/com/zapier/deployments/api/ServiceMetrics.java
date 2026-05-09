package com.zapier.deployments.api;

public record ServiceMetrics(
        int frequency,
        int p95Duration,
        double failureRate
) {
}
