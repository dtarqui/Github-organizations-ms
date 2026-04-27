package com.githubx.Github_organizations_ms.service.contratos;

import com.githubx.Github_organizations_ms.dto.request.CreateTeamRequest;
import com.githubx.Github_organizations_ms.dto.request.UpdateTeamRequest;
import com.githubx.Github_organizations_ms.dto.response.TeamListResponse;
import com.githubx.Github_organizations_ms.dto.response.TeamResponse;

public interface TeamService {

    TeamListResponse listOrgTeams(String orgName);

    TeamResponse createTeam(String orgName, CreateTeamRequest request);

    TeamResponse getTeam(String orgName, String teamId);

    TeamResponse updateTeam(String orgName, String teamId, UpdateTeamRequest request);

    void deleteTeam(String orgName, String teamId);
}