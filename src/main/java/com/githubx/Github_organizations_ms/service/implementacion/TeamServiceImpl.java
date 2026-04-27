package com.githubx.Github_organizations_ms.service.implementacion;

import com.githubx.Github_organizations_ms.config.security.AuthenticatedUserResolver;
import com.githubx.Github_organizations_ms.dao.OrgMemberDao;
import com.githubx.Github_organizations_ms.dao.OrganizationDao;
import com.githubx.Github_organizations_ms.dao.TeamDao;
import com.githubx.Github_organizations_ms.dto.request.CreateTeamRequest;
import com.githubx.Github_organizations_ms.dto.request.UpdateTeamRequest;
import com.githubx.Github_organizations_ms.dto.response.TeamListResponse;
import com.githubx.Github_organizations_ms.dto.response.TeamResponse;
import com.githubx.Github_organizations_ms.mapper.TeamMapper;
import com.githubx.Github_organizations_ms.model.Organization;
import com.githubx.Github_organizations_ms.model.Team;
import com.githubx.Github_organizations_ms.model.TeamPermission;
import com.githubx.Github_organizations_ms.service.contratos.TeamService;
import com.githubx.Github_organizations_ms.util.errorhandling.EntityConflictException;
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
public class TeamServiceImpl implements TeamService {

    private final OrganizationDao organizationDao;
    private final OrgMemberDao orgMemberDao;
    private final TeamDao teamDao;
    private final TeamMapper teamMapper;
    private final AuthenticatedUserResolver userResolver;

    @Override
    @Transactional(readOnly = true)
    public TeamListResponse listOrgTeams(String orgName) {
        Organization org = findOrgOrThrow(orgName);
        assertIsMember(org.getId(), userResolver.getCurrentUserId());

        List<TeamResponse> teams = teamDao.findAllByOrganizationId(org.getId())
                .stream()
                .map(teamMapper::toResponse)
                .toList();

        return new TeamListResponse(teams);
    }

    @Override
    @Transactional
    public TeamResponse createTeam(String orgName, CreateTeamRequest request) {
        UUID currentUserId = userResolver.getCurrentUserId();
        Organization org = findOrgOrThrow(orgName);

        assertIsOwner(org, currentUserId);

        // Nombre único por organización
        if (teamDao.existsByOrganizationIdAndName(org.getId(), request.name())) {
            throw EntityConflictException.teamName(request.name());
        }

        TeamPermission permission = parsePermission(request.permission());

        Team team = Team.builder()
                .organizationId(org.getId())
                .name(request.name())
                .description(request.description())
                .permission(permission)
                .build();

        team = teamDao.save(team);
        log.info("Equipo creado: {} en organización: {}", request.name(), orgName);
        return teamMapper.toResponse(team);
    }

    @Override
    @Transactional(readOnly = true)
    public TeamResponse getTeam(String orgName, String teamId) {
        Organization org = findOrgOrThrow(orgName);
        assertIsMember(org.getId(), userResolver.getCurrentUserId());

        Team team = findTeamOrThrow(teamId, org.getId());
        return teamMapper.toResponse(team);
    }

    @Override
    @Transactional
    public TeamResponse updateTeam(String orgName, String teamId, UpdateTeamRequest request) {
        UUID currentUserId = userResolver.getCurrentUserId();
        Organization org = findOrgOrThrow(orgName);

        assertIsOwner(org, currentUserId);

        Team team = findTeamOrThrow(teamId, org.getId());

        if (request.name() != null) team.setName(request.name());
        if (request.description() != null) team.setDescription(request.description());
        if (request.permission() != null) team.setPermission(parsePermission(request.permission()));

        team = teamDao.save(team);
        log.info("Equipo actualizado: {} en organización: {}", teamId, orgName);
        return teamMapper.toResponse(team);
    }

    @Override
    @Transactional
    public void deleteTeam(String orgName, String teamId) {
        UUID currentUserId = userResolver.getCurrentUserId();
        Organization org = findOrgOrThrow(orgName);

        assertIsOwner(org, currentUserId);

        Team team = findTeamOrThrow(teamId, org.getId());
        teamDao.delete(team);
        log.info("Equipo eliminado: {} de organización: {}", teamId, orgName);
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