package com.githubx.Github_organizations_ms.dto.response;

import java.util.List;

public record OrganizationPageResponse(
        List<OrganizationResponse> organizations,
        PaginationResponse pagination
) {}