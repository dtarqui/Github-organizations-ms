package com.githubx.Github_organizations_ms.util.errorhandling;

public class InvalidEnumValueException extends RuntimeException {

    public InvalidEnumValueException(String field, String value, String validValues) {
        super("Valor inválido para '" + field + "': '" + value + "'. Valores aceptados: " + validValues);
    }
}