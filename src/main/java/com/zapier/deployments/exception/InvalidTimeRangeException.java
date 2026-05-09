package com.zapier.deployments.exception;

public class InvalidTimeRangeException extends RuntimeException {
    public InvalidTimeRangeException(String value) {
        super("Invalid time_range: " + value + ". Use format like 7d or 12h.");
    }
}
