package com.githubx.Github_organizations_ms.service.contratos;

import com.githubx.Github_organizations_ms.generated.model.CreateOrganizationBody;
import com.githubx.Github_organizations_ms.generated.model.ListMyOrganizationsBody;
import com.githubx.Github_organizations_ms.generated.model.OrganizationDTO;
import com.githubx.Github_organizations_ms.generated.model.SearchOrganizationsBody;
import com.githubx.Github_organizations_ms.generated.model.UpdateOrganizationBody;

public interface OrganizationService {

    ListMyOrganizationsBody listMyOrganizations(int page, int perPage);

    OrganizationDTO createOrganization(CreateOrganizationBody request);

    OrganizationDTO getOrganization(String orgName);

    OrganizationDTO updateOrganization(String orgName, UpdateOrganizationBody request);

    void deleteOrganization(String orgName);

    SearchOrganizationsBody searchOrganizations(String query, int page, int perPage);
}
