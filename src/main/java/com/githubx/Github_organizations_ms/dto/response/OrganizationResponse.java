package com.githubx.Github_organizations_ms.dto.response;

import java.util.UUID;

public record OrganizationResponse(
        UUID id,
        String name,
        String displayName,
        String description,
        String avatarUrl,
        String website,
        String visibility,
        int membersCount,
        int reposCount,
        int teamsCount,
        String createdAt,
        String updatedAt
) {}