package com.githubx.Github_organizations_ms.dto.request;

import jakarta.validation.constraints.NotNull;

public record AddTeamRepoRequest(

        @NotNull(message = "El permiso es obligatorio")
        String permission

) {}