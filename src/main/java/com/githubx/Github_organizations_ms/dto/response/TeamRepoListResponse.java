package com.githubx.Github_organizations_ms.dto.response;

import java.util.List;

public record TeamRepoListResponse(
        List<TeamRepoResponse> repos
) {}