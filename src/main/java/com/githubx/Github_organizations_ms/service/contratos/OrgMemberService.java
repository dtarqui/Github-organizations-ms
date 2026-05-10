package com.githubx.Github_organizations_ms.service.contratos;

import com.githubx.Github_organizations_ms.generated.model.AddOrgMemberBody;
import com.githubx.Github_organizations_ms.generated.model.ListOrgMembersBody;
import com.githubx.Github_organizations_ms.generated.model.OrgMemberDTO;
import com.githubx.Github_organizations_ms.generated.model.UpdateOrgMemberRoleBody;

public interface OrgMemberService {

    ListOrgMembersBody listOrgMembers(String orgName);

    OrgMemberDTO addOrgMember(String orgName, AddOrgMemberBody request);

    OrgMemberDTO updateOrgMemberRole(String orgName, String username, UpdateOrgMemberRoleBody request);

    void removeOrgMember(String orgName, String username);
}
