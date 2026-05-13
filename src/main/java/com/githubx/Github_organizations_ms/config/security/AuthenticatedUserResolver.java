package com.githubx.Github_organizations_ms.config.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthenticatedUserResolver {

    public UUID getCurrentUserId() {
        Jwt jwt = getJwtFromSecurityContext();
        String userDbId = jwt.getClaimAsString("user_db_id");
        if (userDbId != null) {
            return UUID.fromString(userDbId);
        }
        return UUID.fromString(jwt.getSubject());
    }

    public String getCurrentUsername() {
        Jwt jwt = getJwtFromSecurityContext();
        String username = jwt.getClaimAsString("preferred_username");
        return username != null ? username : jwt.getSubject();
    }

    public String getCurrentUserEmail() {
        Jwt jwt = getJwtFromSecurityContext();
        return jwt.getClaimAsString("email");
    }

    public Jwt getCurrentJwt() {
        return getJwtFromSecurityContext();
    }

    private Jwt getJwtFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            throw new IllegalStateException("No hay usuario autenticado");
        }
        if (!(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("El principal no es un JWT válido");
        }
        return jwt;
    }
}












