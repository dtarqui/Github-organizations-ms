package com.githubx.Github_organizations_ms.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateOrganizationRequest(

        @Size(min = 1, max = 100, message = "El displayName debe tener entre 1 y 100 caracteres")
        String displayName,

        String description,

        String website,

        String avatarUrl,

        String visibility

) {}