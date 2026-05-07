package com.zapier.deployments.api;

public record ErrorResponse(
        ErrorBody error
) {
    public record ErrorBody(
            String code,
            String message
    ) {
    }
}
