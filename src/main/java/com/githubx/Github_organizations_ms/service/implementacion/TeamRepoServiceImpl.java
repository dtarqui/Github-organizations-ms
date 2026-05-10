package com.githubx.Github_organizations_ms.service.implementacion;

import com.githubx.Github_organizations_ms.config.security.AuthenticatedUserResolver;
import com.githubx.Github_organizations_ms.dao.OrgMemberDao;
import com.githubx.Github_organizations_ms.dao.OrganizationDao;
import com.githubx.Github_organizations_ms.dao.TeamDao;
import com.githubx.Github_organizations_ms.dao.TeamRepoDao;
import com.githubx.Github_organizations_ms.generated.model.AddTeamRepoBody;
import com.githubx.Github_organizations_ms.generated.model.ListOrgReposBody;
import com.githubx.Github_organizations_ms.generated.model.ListTeamReposBody;
import com.githubx.Github_organizations_ms.generated.model.PaginationMeta;
import com.githubx.Github_organizations_ms.generated.model.TeamRepoDTO;
import com.githubx.Github_organizations_ms.mapper.TeamRepoMapper;
import com.githubx.Github_organizations_ms.model.Organization;
import com.githubx.Github_organizations_ms.model.Team;
import com.githubx.Github_organizations_ms.model.TeamPermission;
import com.githubx.Github_organizations_ms.model.TeamRepo;
import com.githubx.Github_organizations_ms.service.contratos.TeamRepoService;
import com.githubx.Github_organizations_ms.util.errorhandling.EntityNotFoundException;
import com.githubx.Github_organizations_ms.util.errorhandling.ForbiddenOperationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamRepoServiceImpl implements TeamRepoService {

    private final OrganizationDao organizationDao;
    private final OrgMemberDao orgMemberDao;
    private final TeamDao teamDao;
    private final TeamRepoDao teamRepoDao;
    private final TeamRepoMapper teamRepoMapper;
    private final AuthenticatedUserResolver userResolver;

    @Override
    @Transactional(readOnly = true)
    public ListTeamReposBody listTeamRepos(String orgName, String teamId) {
        Organization org = findOrgOrThrow(orgName);
        assertIsMember(org.getId(), userResolver.getCurrentUserId());

        Team team = findTeamOrThrow(teamId, org.getId());

        List<TeamRepoDTO> repos = teamRepoDao.findAllByTeamId(team.getId())
                .stream()
                .map(teamRepoMapper::toDto)
                .toList();

        return new ListTeamReposBody().repos(repos);
    }

    @Override
    @Transactional
    public void addTeamRepo(String orgName, String teamId, String repoName, AddTeamRepoBody request) {
        UUID currentUserId = userResolver.getCurrentUserId();
        Organization org = findOrgOrThrow(orgName);
        assertIsOwner(org, currentUserId);

        Team team = findTeamOrThrow(teamId, org.getId());
        TeamPermission permission = TeamPermission.valueOf(request.getPermission().name());

        TeamRepo teamRepo = teamRepoDao.findByTeamIdAndRepoName(team.getId(), repoName)
                .orElse(TeamRepo.builder()
                        .teamId(team.getId())
                        .repoId(UUID.randomUUID()) // TODO: resolver repoId desde ms-repos
                        .repoName(repoName)
                        .fullName(org.getName() + "/" + repoName)
                        .build());

        teamRepo.setPermission(permission);
        teamRepoDao.save(teamRepo);
        log.info("Repo: {} asignado al equipo: {} con permiso: {}", repoName, teamId, permission);
    }

    @Override
    @Transactional
    public void removeTeamRepo(String orgName, String teamId, String repoName) {
        UUID currentUserId = userResolver.getCurrentUserId();
        Organization org = findOrgOrThrow(orgName);
        assertIsOwner(org, currentUserId);

        Team team = findTeamOrThrow(teamId, org.getId());
        teamRepoDao.deleteByTeamIdAndRepoName(team.getId(), repoName);
        log.info("Repo: {} removido del equipo: {}", repoName, teamId);
    }

    @Override
    @Transactional(readOnly = true)
    public ListOrgReposBody listOrgRepos(String orgName, int page, int perPage) {
        // Los repositorios viven en otro microservicio.
        // TODO: llamar a ms-repos para obtener los repos de la organización.
        findOrgOrThrow(orgName);

        PaginationMeta pagination = new PaginationMeta()
                .page(page)
                .perPage(perPage)
                .total(0)
                .totalPages(0);

        return new ListOrgReposBody()
                .repositories(List.of())
                .pagination(pagination);
    }

    private Organization findOrgOrThrow(String orgName) {
        return organizationDao.findByName(orgName)
                .orElseThrow(() -> EntityNotFoundException.organization(orgName));
    }

    private Team findTeamOrThrow(String teamId, UUID orgId) {
        return teamDao.findByIdAndOrganizationId(UUID.fromString(teamId), orgId)
                .orElseThrow(() -> EntityNotFoundException.team(teamId));
    }

    private void assertIsOwner(Organization org, UUID userId) {
        if (!org.getOwnerId().equals(userId)) {
            throw ForbiddenOperationException.notOwner();
        }
    }

    private void assertIsMember(UUID orgId, UUID userId) {
        if (!orgMemberDao.existsByOrganizationIdAndUserId(orgId, userId)) {
            throw ForbiddenOperationException.notMember();
        }
    }
}
