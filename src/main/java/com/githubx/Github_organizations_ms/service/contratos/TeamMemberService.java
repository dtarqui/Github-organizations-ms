package com.githubx.Github_organizations_ms.service.contratos;

import com.githubx.Github_organizations_ms.generated.model.ListTeamMembersBody;

public interface TeamMemberService {

    ListTeamMembersBody listTeamMembers(String orgName, String teamId);

    void addTeamMember(String orgName, String teamId, String username);

    void removeTeamMember(String orgName, String teamId, String username);
}
