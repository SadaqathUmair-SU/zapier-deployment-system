package com.zapier.deployments.api;

public record ApiResponse<T>(
        T data
) {
}
