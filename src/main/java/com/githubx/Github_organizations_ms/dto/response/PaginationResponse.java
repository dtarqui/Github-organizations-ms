package com.githubx.Github_organizations_ms.dto.response;

public record PaginationResponse(
        int page,
        int perPage,
        long totalElements,
        int totalPages
) {}