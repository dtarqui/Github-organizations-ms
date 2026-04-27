package com.githubx.Github_organizations_ms.dto.response;

import java.util.UUID;

public record TeamRepoResponse(
        UUID repoId,
        String repoName,
        String fullName,
        String permission,
        String assignedAt
) {}