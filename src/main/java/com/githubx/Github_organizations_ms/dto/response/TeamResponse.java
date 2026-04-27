package com.githubx.Github_organizations_ms.dto.response;

import java.util.UUID;

public record TeamResponse(
        UUID id,
        UUID orgId,
        String name,
        String description,
        String permission,
        int membersCount,
        int reposCount,
        String createdAt
) {}