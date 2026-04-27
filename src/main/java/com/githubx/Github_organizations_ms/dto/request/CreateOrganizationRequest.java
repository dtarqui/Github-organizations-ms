package com.githubx.Github_organizations_ms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateOrganizationRequest(

        @NotBlank(message = "El nombre de la organización es obligatorio")
        @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
        String name,

        @NotBlank(message = "El nombre para mostrar es obligatorio")
        @Size(min = 1, max = 100, message = "El displayName debe tener entre 1 y 100 caracteres")
        String displayName,

        String description,

        String website,

        @NotNull(message = "La visibilidad es obligatoria")
        String visibility

) {}