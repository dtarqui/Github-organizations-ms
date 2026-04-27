package com.githubx.Github_organizations_ms.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );
        return http.build();
    }
}














// package com.githubx.Github_organizations_ms.config.security;

// import lombok.RequiredArgsConstructor;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.http.HttpMethod;
// import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.context.annotation.Profile;

// @Configuration
// @EnableWebSecurity
// @EnableMethodSecurity
// @RequiredArgsConstructor
// @Profile("!dev")  
// public class SecurityConfig {

//     private final JwtAuthConverter jwtAuthConverter;

//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//         http
//             // API stateless: sin sesión ni CSRF
//             .csrf(AbstractHttpConfigurer::disable)
//             .sessionManagement(session ->
//                 session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

//             .authorizeHttpRequests(auth -> auth
//                 // ===== Rutas públicas (OrgPublicApi del smithy) =====
//                 .requestMatchers(HttpMethod.GET, "/v1/orgs/{orgName}").permitAll()
//                 .requestMatchers(HttpMethod.GET, "/v1/orgs/{orgName}/repos").permitAll()

//                 // ===== OpenAPI / Swagger =====
//                 .requestMatchers(
//                     "/v3/api-docs/**",
//                     "/swagger-ui/**",
//                     "/swagger-ui.html"
//                 ).permitAll()

//                 // ===== Actuator =====
//                 .requestMatchers("/actuator/health", "/actuator/info").permitAll()

//                 // ===== Todo lo demás requiere autenticación =====
//                 .anyRequest().authenticated()
//             )

//             // ===== OAuth2 Resource Server con JWT =====
//             .oauth2ResourceServer(oauth2 -> oauth2
//                 .jwt(jwt -> jwt.jwtAuthenticationConverter(buildJwtConverter()))
//             );

//         return http.build();
//     }

//     private JwtAuthenticationConverter buildJwtConverter() {
//         JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
//         converter.setJwtGrantedAuthoritiesConverter(jwtAuthConverter);
//         return converter;
//     }
// }