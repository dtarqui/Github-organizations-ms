package com.githubx.Github_organizations_ms.util.errorhandling;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public static EntityNotFoundException organization(String orgName) {
        return new EntityNotFoundException("Organización no encontrada: " + orgName);
    }

    public static EntityNotFoundException team(String teamId) {
        return new EntityNotFoundException("Equipo no encontrado: " + teamId);
    }

    public static EntityNotFoundException member(String username) {
        return new EntityNotFoundException("Miembro no encontrado: " + username);
    }
}