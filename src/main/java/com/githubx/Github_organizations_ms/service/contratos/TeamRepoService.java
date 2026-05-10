package com.githubx.Github_organizations_ms.service.contratos;

import com.githubx.Github_organizations_ms.generated.model.AddTeamRepoBody;
import com.githubx.Github_organizations_ms.generated.model.ListOrgReposBody;
import com.githubx.Github_organizations_ms.generated.model.ListTeamReposBody;

public interface TeamRepoService {

    ListTeamReposBody listTeamRepos(String orgName, String teamId);

    void addTeamRepo(String orgName, String teamId, String repoName, AddTeamRepoBody request);

    void removeTeamRepo(String orgName, String teamId, String repoName);

    ListOrgReposBody listOrgRepos(String orgName, int page, int perPage);
}
