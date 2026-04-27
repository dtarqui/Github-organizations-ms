package com.githubx.Github_organizations_ms.service.contratos;

import com.githubx.Github_organizations_ms.dto.request.CreateOrganizationRequest;
import com.githubx.Github_organizations_ms.dto.request.UpdateOrganizationRequest;
import com.githubx.Github_organizations_ms.dto.response.OrganizationPageResponse;
import com.githubx.Github_organizations_ms.dto.response.OrganizationResponse;

public interface OrganizationService {

    OrganizationPageResponse listMyOrganizations(int page, int perPage);

    OrganizationResponse createOrganization(CreateOrganizationRequest request);

    OrganizationResponse getOrganization(String orgName);

    OrganizationResponse updateOrganization(String orgName, UpdateOrganizationRequest request);

    void deleteOrganization(String orgName);
}