package com.githubx.Github_organizations_ms.service.implementacion;

import com.githubx.Github_organizations_ms.config.security.AuthenticatedUserResolver;
import com.githubx.Github_organizations_ms.dao.OrgMemberDao;
import com.githubx.Github_organizations_ms.dao.OrganizationDao;
import com.githubx.Github_organizations_ms.dao.TeamDao;
import com.githubx.Github_organizations_ms.dao.TeamMemberDao;
import com.githubx.Github_organizations_ms.dto.response.TeamMemberListResponse;
import com.githubx.Github_organizations_ms.dto.response.TeamMemberResponse;
import com.githubx.Github_organizations_ms.mapper.TeamMemberMapper;
import com.githubx.Github_organizations_ms.model.OrgMember;
import com.githubx.Github_organizations_ms.model.Organization;
import com.githubx.Github_organizations_ms.model.Team;
import com.githubx.Github_organizations_ms.model.TeamMember;
import com.githubx.Github_organizations_ms.service.contratos.TeamMemberService;
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
public class TeamMemberServiceImpl implements TeamMemberService {

    private final OrganizationDao organizationDao;
    private final OrgMemberDao orgMemberDao;
    private final TeamDao teamDao;
    private final TeamMemberDao teamMemberDao;
    private final TeamMemberMapper teamMemberMapper;
    private final AuthenticatedUserResolver userResolver;

    @Override
    @Transactional(readOnly = true)
    public TeamMemberListResponse listTeamMembers(String orgName, String teamId) {
        Organization org = findOrgOrThrow(orgName);
        assertIsMember(org.getId(), userResolver.getCurrentUserId());

        Team team = findTeamOrThrow(teamId, org.getId());

        List<TeamMemberResponse> members = teamMemberDao.findAllByTeamId(team.getId())
                .stream()
                .map(teamMemberMapper::toResponse)
                .toList();

        return new TeamMemberListResponse(members);
    }

    @Override
    @Transactional
    public void addTeamMember(String orgName, String teamId, String username) {
        UUID currentUserId = userResolver.getCurrentUserId();
        Organization org = findOrgOrThrow(orgName);
        assertIsOwner(org, currentUserId);

        Team team = findTeamOrThrow(teamId, org.getId());

        // El usuario debe ser miembro de la organización primero
        OrgMember orgMember = orgMemberDao.findByOrganizationIdAndUsername(org.getId(), username)
                .orElseThrow(() -> EntityNotFoundException.member(username));

        // Verificar que no sea ya miembro del equipo
        if (teamMemberDao.existsByTeamIdAndUserId(team.getId(), orgMember.getUserId())) {
            throw EntityConflictException.teamMemberAlreadyExists(username);
        }

        TeamMember teamMember = TeamMember.builder()
                .teamId(team.getId())
                .userId(orgMember.getUserId())
                .username(orgMember.getUsername())
                .avatarUrl(orgMember.getAvatarUrl())
                .build();

        teamMemberDao.save(teamMember);
        log.info("Miembro: {} agregado al equipo: {} en organización: {}", username, teamId, orgName);
    }

    @Override
    @Transactional
    public void removeTeamMember(String orgName, String teamId, String username) {
        UUID currentUserId = userResolver.getCurrentUserId();
        Organization org = findOrgOrThrow(orgName);
        assertIsOwner(org, currentUserId);

        Team team = findTeamOrThrow(teamId, org.getId());

        OrgMember orgMember = orgMemberDao.findByOrganizationIdAndUsername(org.getId(), username)
                .orElseThrow(() -> EntityNotFoundException.member(username));

        teamMemberDao.deleteByTeamIdAndUserId(team.getId(), orgMember.getUserId());
        log.info("Miembro: {} eliminado del equipo: {} (organización: {})", username, teamId, orgName);
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
}