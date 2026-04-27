package com.githubx.Github_organizations_ms.dto.response;

import java.util.UUID;

public record OrgRepoResponse(
        UUID id,
        String name,
        String fullName,
        String description,
        int starsCount,
        String updatedAt
) {}