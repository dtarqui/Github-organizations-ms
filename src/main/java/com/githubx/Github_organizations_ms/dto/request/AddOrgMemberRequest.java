package com.githubx.Github_organizations_ms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddOrgMemberRequest(

        @NotBlank(message = "El username es obligatorio")
        String username,

        @NotNull(message = "El rol es obligatorio")
        String role

) {}