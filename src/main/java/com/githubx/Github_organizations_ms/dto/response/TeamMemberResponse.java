package com.githubx.Github_organizations_ms.dto.response;

import java.util.UUID;

public record TeamMemberResponse(
        UUID userId,
        String username,
        String avatarUrl,
        String addedAt
) {}