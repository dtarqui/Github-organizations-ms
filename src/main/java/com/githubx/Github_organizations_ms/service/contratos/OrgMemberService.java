package com.githubx.Github_organizations_ms.service.contratos;

import com.githubx.Github_organizations_ms.dto.request.AddOrgMemberRequest;
import com.githubx.Github_organizations_ms.dto.request.UpdateOrgMemberRoleRequest;
import com.githubx.Github_organizations_ms.dto.response.OrgMemberListResponse;
import com.githubx.Github_organizations_ms.dto.response.OrgMemberResponse;

public interface OrgMemberService {

    OrgMemberListResponse listOrgMembers(String orgName);

    OrgMemberResponse addOrgMember(String orgName, AddOrgMemberRequest request);

    OrgMemberResponse updateOrgMemberRole(String orgName, String username, UpdateOrgMemberRoleRequest request);

    void removeOrgMember(String orgName, String username);
}