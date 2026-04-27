package com.githubx.Github_organizations_ms.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateTeamRequest(

        @Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres")
        String name,

        String description,

        String permission

) {}