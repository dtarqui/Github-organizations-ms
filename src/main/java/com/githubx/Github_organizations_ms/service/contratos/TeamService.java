package com.githubx.Github_organizations_ms.service.contratos;

import com.githubx.Github_organizations_ms.generated.model.CreateTeamBody;
import com.githubx.Github_organizations_ms.generated.model.ListOrgTeamsBody;
import com.githubx.Github_organizations_ms.generated.model.TeamDTO;
import com.githubx.Github_organizations_ms.generated.model.UpdateTeamBody;

public interface TeamService {

    ListOrgTeamsBody listOrgTeams(String orgName);

    TeamDTO createTeam(String orgName, CreateTeamBody request);

    TeamDTO getTeam(String orgName, String teamId);

    TeamDTO updateTeam(String orgName, String teamId, UpdateTeamBody request);

    void deleteTeam(String orgName, String teamId);
}
