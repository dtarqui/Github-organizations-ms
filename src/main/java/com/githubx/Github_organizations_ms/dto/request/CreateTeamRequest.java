package com.githubx.Github_organizations_ms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTeamRequest(

        @NotBlank(message = "El nombre del equipo es obligatorio")
        @Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres")
        String name,

        String description,

        @NotNull(message = "El permiso del equipo es obligatorio")
        String permission

) {}