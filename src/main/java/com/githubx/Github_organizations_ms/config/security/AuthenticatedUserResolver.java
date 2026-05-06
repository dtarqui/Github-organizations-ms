package com.githubx.Github_organizations_ms.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthenticatedUserResolver {

    private static final UUID DEV_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final String DEV_USERNAME = "dev-user";

    public UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return DEV_USER_ID;
        }
        return UUID.fromString(jwt.getSubject());
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return DEV_USERNAME;
        }
        String username = jwt.getClaimAsString("preferred_username");
        return username != null ? username : DEV_USERNAME;
    }
}












