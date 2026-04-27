package com.githubx.Github_organizations_ms.service.implementacion;

import com.githubx.Github_organizations_ms.config.security.AuthenticatedUserResolver;
import com.githubx.Github_organizations_ms.dao.OrgMemberDao;
import com.githubx.Github_organizations_ms.dao.OrganizationDao;
import com.githubx.Github_organizations_ms.dao.TeamDao;
import com.githubx.Github_organizations_ms.dao.TeamRepoDao;
import com.githubx.Github_organizations_ms.dto.request.AddTeamRepoRequest;
import com.githubx.Github_organizations_ms.dto.response.OrgRepoPageResponse;
import com.githubx.Github_organizations_ms.dto.response.OrgRepoResponse;
import com.githubx.Github_organizations_ms.dto.response.PaginationResponse;
import com.githubx.Github_organizations_ms.dto.response.TeamRepoListResponse;
import com.githubx.Github_organizations_ms.dto.response.TeamRepoResponse;
import com.githubx.Github_organizations_ms.mapper.TeamRepoMapper;
import com.githubx.Github_organizations_ms.model.Organization;
import com.githubx.Github_organizations_ms.model.Team;
import com.githubx.Github_organizations_ms.model.TeamPermission;
import com.githubx.Github_organizations_ms.model.TeamRepo;
import com.githubx.Github_organizations_ms.service.contratos.TeamRepoService;
import com.githubx.Github_organizations_ms.util.errorhandling.EntityNotFoundException;
import com.githubx.Github_organizations_ms.util.errorhandling.ForbiddenOperationException;
import com.githubx.Github_organizations_ms.util.errorhandling.InvalidEnumValueException;
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
    public TeamRepoListResponse listTeamRepos(String orgName, String teamId) {
        Organization org = findOrgOrThrow(orgName);
        assertIsMember(org.getId(), userResolver.getCurrentUserId());

        Team team = findTeamOrThrow(teamId, org.getId());

        List<TeamRepoResponse> repos = teamRepoDao.findAllByTeamId(team.getId())
                .stream()
                .map(teamRepoMapper::toResponse)
                .toList();

        return new TeamRepoListResponse(repos);
    }

    @Override
    @Transactional
    public void addTeamRepo(String orgName, String teamId, String repoName, AddTeamRepoRequest request) {
        UUID currentUserId = userResolver.getCurrentUserId();
        Organization org = findOrgOrThrow(orgName);
        assertIsOwner(org, currentUserId);

        Team team = findTeamOrThrow(teamId, org.getId());
        TeamPermission permission = parsePermission(request.permission());

        // Si ya existe, actualizar permiso (PUT es idempotente)
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
    public OrgRepoPageResponse listOrgRepos(String orgName, int page, int perPage) {
        // Los repositorios viven en otro microservicio.
        // Este endpoint devuelve una lista vacía hasta integrar ms-repos vía gRPC o REST.
        // TODO: llamar a ms-repos para obtener los repos de la organización.
        Organization org = findOrgOrThrow(orgName);

        PaginationResponse pagination = new PaginationResponse(page, perPage, 0, 0);
        return new OrgRepoPageResponse(List.of(), pagination);
    }

    // ===== Helpers privados =====

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

    private TeamPermission parsePermission(String value) {
        try {
            return TeamPermission.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidEnumValueException("permission", value, "read, write, admin");
        }
    }
}