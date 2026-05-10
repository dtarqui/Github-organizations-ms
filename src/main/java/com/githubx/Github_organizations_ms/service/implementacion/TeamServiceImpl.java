package com.githubx.Github_organizations_ms.service.implementacion;

import com.githubx.Github_organizations_ms.config.security.AuthenticatedUserResolver;
import com.githubx.Github_organizations_ms.dao.OrgMemberDao;
import com.githubx.Github_organizations_ms.dao.OrganizationDao;
import com.githubx.Github_organizations_ms.dao.TeamDao;
import com.githubx.Github_organizations_ms.generated.model.CreateTeamBody;
import com.githubx.Github_organizations_ms.generated.model.ListOrgTeamsBody;
import com.githubx.Github_organizations_ms.generated.model.TeamDTO;
import com.githubx.Github_organizations_ms.generated.model.UpdateTeamBody;
import com.githubx.Github_organizations_ms.mapper.TeamMapper;
import com.githubx.Github_organizations_ms.model.Organization;
import com.githubx.Github_organizations_ms.model.Team;
import com.githubx.Github_organizations_ms.model.TeamPermission;
import com.githubx.Github_organizations_ms.service.contratos.TeamService;
import com.githubx.Github_organizations_ms.util.errorhandling.EntityConflictException;
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
public class TeamServiceImpl implements TeamService {

    private final OrganizationDao organizationDao;
    private final OrgMemberDao orgMemberDao;
    private final TeamDao teamDao;
    private final TeamMapper teamMapper;
    private final AuthenticatedUserResolver userResolver;

    @Override
    @Transactional(readOnly = true)
    public ListOrgTeamsBody listOrgTeams(String orgName) {
        Organization org = findOrgOrThrow(orgName);
        assertIsMember(org.getId(), userResolver.getCurrentUserId());

        List<TeamDTO> teams = teamDao.findAllByOrganizationId(org.getId())
                .stream()
                .map(teamMapper::toDto)
                .toList();

        return new ListOrgTeamsBody().teams(teams);
    }

    @Override
    @Transactional
    public TeamDTO createTeam(String orgName, CreateTeamBody request) {
        UUID currentUserId = userResolver.getCurrentUserId();
        Organization org = findOrgOrThrow(orgName);

        assertIsOwner(org, currentUserId);

        if (teamDao.existsByOrganizationIdAndName(org.getId(), request.getName())) {
            throw EntityConflictException.teamName(request.getName());
        }

        TeamPermission permission = TeamPermission.valueOf(request.getPermission().name());

        Team team = Team.builder()
                .organizationId(org.getId())
                .name(request.getName())
                .description(request.getDescription())
                .permission(permission)
                .build();

        team = teamDao.save(team);
        log.info("Equipo creado: {} en organización: {}", request.getName(), orgName);
        return teamMapper.toDto(team);
    }

    @Override
    @Transactional(readOnly = true)
    public TeamDTO getTeam(String orgName, String teamId) {
        Organization org = findOrgOrThrow(orgName);
        assertIsMember(org.getId(), userResolver.getCurrentUserId());

        Team team = findTeamOrThrow(teamId, org.getId());
        return teamMapper.toDto(team);
    }

    @Override
    @Transactional
    public TeamDTO updateTeam(String orgName, String teamId, UpdateTeamBody request) {
        UUID currentUserId = userResolver.getCurrentUserId();
        Organization org = findOrgOrThrow(orgName);

        assertIsOwner(org, currentUserId);

        Team team = findTeamOrThrow(teamId, org.getId());

        if (request.getName() != null) team.setName(request.getName());
        if (request.getDescription() != null) team.setDescription(request.getDescription());
        if (request.getPermission() != null)
            team.setPermission(TeamPermission.valueOf(request.getPermission().name()));

        team = teamDao.save(team);
        log.info("Equipo actualizado: {} en organización: {}", teamId, orgName);
        return teamMapper.toDto(team);
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
