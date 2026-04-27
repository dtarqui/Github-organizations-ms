package com.githubx.Github_organizations_ms.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateOrgMemberRoleRequest(

        @NotNull(message = "El rol es obligatorio")
        String role

) {}