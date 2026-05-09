package com.githubx.Github_organizations_ms.service.implementacion;

import com.githubx.Github_organizations_ms.config.security.AuthenticatedUserResolver;
import com.githubx.Github_organizations_ms.dao.OrgMemberDao;
import com.githubx.Github_organizations_ms.dao.OrganizationDao;
import com.githubx.Github_organizations_ms.dao.TeamDao;
import com.githubx.Github_organizations_ms.dao.TeamMemberDao;
import com.githubx.Github_organizations_ms.generated.model.AddOrgMemberBody;
import com.githubx.Github_organizations_ms.generated.model.ListOrgMembersBody;
import com.githubx.Github_organizations_ms.generated.model.OrgMemberDTO;
import com.githubx.Github_organizations_ms.generated.model.UpdateOrgMemberRoleBody;
import com.githubx.Github_organizations_ms.mapper.OrgMemberMapper;
import com.githubx.Github_organizations_ms.model.OrgMember;
import com.githubx.Github_organizations_ms.model.OrgMemberRole;
import com.githubx.Github_organizations_ms.model.Organization;
import com.githubx.Github_organizations_ms.model.Team;
import com.githubx.Github_organizations_ms.service.contratos.OrgMemberService;
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
public class OrgMemberServiceImpl implements OrgMemberService {

    private final OrganizationDao organizationDao;
    private final OrgMemberDao orgMemberDao;
    private final TeamDao teamDao;
    private final TeamMemberDao teamMemberDao;
    private final OrgMemberMapper orgMemberMapper;
    private final AuthenticatedUserResolver userResolver;

    @Override
    @Transactional(readOnly = true)
    public ListOrgMembersBody listOrgMembers(String orgName) {
        Organization org = findOrgOrThrow(orgName);
        assertIsMember(org.getId(), userResolver.getCurrentUserId());

        List<OrgMemberDTO> members = orgMemberDao.findAllByOrganizationId(org.getId())
                .stream()
                .map(orgMemberMapper::toDto)
                .toList();

        return new ListOrgMembersBody().members(members);
    }

    @Override
    @Transactional
    public OrgMemberDTO addOrgMember(String orgName, AddOrgMemberBody request) {
        UUID currentUserId = userResolver.getCurrentUserId();
        Organization org = findOrgOrThrow(orgName);

        assertIsOwner(org, currentUserId);

        if (orgMemberDao.existsByOrganizationIdAndUsername(org.getId(), request.getUsername())) {
            throw EntityConflictException.memberAlreadyExists(request.getUsername());
        }

        OrgMemberRole role = OrgMemberRole.valueOf(request.getRole().name());

        UUID newMemberUserId = UUID.randomUUID(); // TODO: resolver desde ms-usuarios

        OrgMember member = OrgMember.builder()
                .organizationId(org.getId())
                .userId(newMemberUserId)
                .username(request.getUsername())
                .role(role)
                .build();

        member = orgMemberDao.save(member);
        log.info("Miembro agregado: {} a organización: {}", request.getUsername(), orgName);
        return orgMemberMapper.toDto(member);
    }

    @Override
    @Transactional
    public OrgMemberDTO updateOrgMemberRole(String orgName, String username, UpdateOrgMemberRoleBody request) {
        UUID currentUserId = userResolver.getCurrentUserId();
        Organization org = findOrgOrThrow(orgName);

        assertIsOwner(org, currentUserId);

        OrgMember member = orgMemberDao.findByOrganizationIdAndUsername(org.getId(), username)
                .orElseThrow(() -> EntityNotFoundException.member(username));

        member.setRole(OrgMemberRole.valueOf(request.getRole().name()));
        member = orgMemberDao.save(member);

        log.info("Rol actualizado para: {} en organización: {}", username, orgName);
        return orgMemberMapper.toDto(member);
    }

    @Override
    @Transactional
    public void removeOrgMember(String orgName, String username) {
        UUID currentUserId = userResolver.getCurrentUserId();
        Organization org = findOrgOrThrow(orgName);

        assertIsOwner(org, currentUserId);

        OrgMember member = orgMemberDao.findByOrganizationIdAndUsername(org.getId(), username)
                .orElseThrow(() -> EntityNotFoundException.member(username));

        List<UUID> orgTeamIds = teamDao.findAllByOrganizationId(org.getId())
                .stream().map(Team::getId).toList();

        if (!orgTeamIds.isEmpty()) {
            teamMemberDao.deleteByTeamIdInAndUserId(orgTeamIds, member.getUserId());
        }

        orgMemberDao.deleteByOrganizationIdAndUserId(org.getId(), member.getUserId());
        log.info("Miembro eliminado: {} de organización: {}", username, orgName);
    }

    private Organization findOrgOrThrow(String orgName) {
        return organizationDao.findByName(orgName)
                .orElseThrow(() -> EntityNotFoundException.organization(orgName));
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
