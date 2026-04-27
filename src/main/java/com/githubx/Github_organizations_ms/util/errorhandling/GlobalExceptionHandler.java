package com.githubx.Github_organizations_ms.util.errorhandling;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ===== 400 - Validación de campos =====
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<ErrorResponse.FieldErrorDetail> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> new ErrorResponse.FieldErrorDetail(fe.getField(), fe.getDefaultMessage()))
                .toList();

        ErrorResponse body = ErrorResponse.ofValidation(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Errores de validación en la solicitud",
                request.getRequestURI(),
                fieldErrors
        );
        return ResponseEntity.badRequest().body(body);
    }

    // ===== 400 - Enum inválido =====
    @ExceptionHandler(InvalidEnumValueException.class)
    public ResponseEntity<ErrorResponse> handleInvalidEnum(
            InvalidEnumValueException ex, HttpServletRequest request) {

        return ResponseEntity.badRequest().body(
                ErrorResponse.of(400, "Bad Request", ex.getMessage(), request.getRequestURI())
        );
    }

    // ===== 403 - Operación prohibida =====
    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(
            ForbiddenOperationException ex, HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ErrorResponse.of(403, "Forbidden", ex.getMessage(), request.getRequestURI())
        );
    }

    // ===== 404 - No encontrado =====
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            EntityNotFoundException ex, HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse.of(404, "Not Found", ex.getMessage(), request.getRequestURI())
        );
    }

    // ===== 409 - Conflicto =====
    @ExceptionHandler(EntityConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(
            EntityConflictException ex, HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ErrorResponse.of(409, "Conflict", ex.getMessage(), request.getRequestURI())
        );
    }

    // ===== 500 - Error inesperado =====
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(
            Exception ex, HttpServletRequest request) {

        log.error("Error inesperado en {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponse.of(500, "Internal Server Error",
                        "Ocurrió un error inesperado. Por favor intente más tarde.",
                        request.getRequestURI())
        );
    }
}