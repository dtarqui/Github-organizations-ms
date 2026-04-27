package com.githubx.Github_organizations_ms.util.errorhandling;

public class ForbiddenOperationException extends RuntimeException {

    public ForbiddenOperationException(String message) {
        super(message);
    }

    public static ForbiddenOperationException notOwner() {
        return new ForbiddenOperationException("Solo el owner de la organización puede realizar esta operación.");
    }

    public static ForbiddenOperationException notMember() {
        return new ForbiddenOperationException("No eres miembro de esta organización.");
    }
}