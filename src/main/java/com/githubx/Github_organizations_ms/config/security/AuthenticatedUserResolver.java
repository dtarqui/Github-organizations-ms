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

















// package com.githubx.Github_organizations_ms.config.security;

// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.oauth2.jwt.Jwt;
// import org.springframework.stereotype.Component;

// import java.util.UUID;

// /**
//  * Utilitario para extraer datos del usuario autenticado desde el JWT.
//  * Centraliza el acceso al SecurityContext para no repetirlo en los servicios.
//  */
// @Component
// public class AuthenticatedUserResolver {

//     /**
//      * Retorna el UUID del usuario autenticado (claim "sub" del JWT).
//      */
//     public UUID getCurrentUserId() {
//         Jwt jwt = getJwt();
//         return UUID.fromString(jwt.getSubject());
//     }

//     /**
//      * Retorna el username del usuario autenticado (claim "preferred_username" de Keycloak).
//      * Adaptar el claim según el proveedor de identidad.
//      */
//     public String getCurrentUsername() {
//         Jwt jwt = getJwt();
//         return jwt.getClaimAsString("preferred_username");
//     }

//     private Jwt getJwt() {
//         return (Jwt) SecurityContextHolder.getContext()
//                 .getAuthentication()
//                 .getPrincipal();
//     }
// }