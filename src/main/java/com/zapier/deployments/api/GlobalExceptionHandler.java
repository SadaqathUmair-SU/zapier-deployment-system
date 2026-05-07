package com.zapier.deployments.api;

import com.zapier.deployments.exception.DeploymentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DeploymentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(DeploymentNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(new ErrorResponse.ErrorBody("NOT_FOUND", ex.getMessage())));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFilter(MethodArgumentTypeMismatchException ex) {
        String message = "Invalid status filter: " + ex.getValue() + ". Allowed values: SUCCESS, FAILED, CANCELLED";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(new ErrorResponse.ErrorBody("BAD_REQUEST", message)));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleUnknownPath(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(new ErrorResponse.ErrorBody("NOT_FOUND", "Resource not found")));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        new ErrorResponse.ErrorBody("INTERNAL_SERVER_ERROR", "Unexpected server error")
                ));
    }
}
