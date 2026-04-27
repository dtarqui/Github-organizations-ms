package com.githubx.Github_organizations_ms.util.errorhandling;

public class EntityConflictException extends RuntimeException {

    public EntityConflictException(String message) {
        super(message);
    }

    public static EntityConflictException organizationName(String name) {
        return new EntityConflictException("Ya existe una organización con el nombre: " + name);
    }

    public static EntityConflictException memberAlreadyExists(String username) {
        return new EntityConflictException("El usuario ya es miembro de la organización: " + username);
    }

    public static EntityConflictException teamName(String name) {
        return new EntityConflictException("Ya existe un equipo con el nombre: " + name);
    }

    public static EntityConflictException teamMemberAlreadyExists(String username) {
        return new EntityConflictException("El usuario ya es miembro del equipo: " + username);
    }
}