package com.githubx.Github_organizations_ms.util.errorhandling;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        int status,
        String error,
        String message,
        String path,
        Instant timestamp,
        List<FieldErrorDetail> fieldErrors
) {
    public record FieldErrorDetail(String field, String message) {}

    // Constructor sin fieldErrors para errores simples
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(status, error, message, path, Instant.now(), List.of());
    }

    // Constructor con fieldErrors para errores de validación
    public static ErrorResponse ofValidation(int status, String error, String message, String path,
                                             List<FieldErrorDetail> fieldErrors) {
        return new ErrorResponse(status, error, message, path, Instant.now(), fieldErrors);
    }
}