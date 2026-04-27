package com.githubx.Github_organizations_ms.service.contratos;

import com.githubx.Github_organizations_ms.dto.request.AddTeamRepoRequest;
import com.githubx.Github_organizations_ms.dto.response.OrgRepoPageResponse;
import com.githubx.Github_organizations_ms.dto.response.TeamRepoListResponse;

public interface TeamRepoService {

    TeamRepoListResponse listTeamRepos(String orgName, String teamId);

    void addTeamRepo(String orgName, String teamId, String repoName, AddTeamRepoRequest request);

    void removeTeamRepo(String orgName, String teamId, String repoName);

    OrgRepoPageResponse listOrgRepos(String orgName, int page, int perPage);
}