package com.githubx.Github_organizations_ms.dto.response;

import java.util.List;

public record OrgRepoPageResponse(
        List<OrgRepoResponse> repositories,
        PaginationResponse pagination
) {}