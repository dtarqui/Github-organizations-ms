package com.githubx.Github_organizations_ms.service.implementacion;

import com.githubx.Github_organizations_ms.config.security.AuthenticatedUserResolver;
import com.githubx.Github_organizations_ms.dao.OrgMemberDao;
import com.githubx.Github_organizations_ms.dao.OrganizationDao;
import com.githubx.Github_organizations_ms.dao.TeamDao;
import com.githubx.Github_organizations_ms.dao.TeamMemberDao;
import com.githubx.Github_organizations_ms.dto.request.AddOrgMemberRequest;
import com.githubx.Github_organizations_ms.dto.request.UpdateOrgMemberRoleRequest;
import com.githubx.Github_organizations_ms.dto.response.OrgMemberListResponse;
import com.githubx.Github_organizations_ms.dto.response.OrgMemberResponse;
import com.githubx.Github_organizations_ms.mapper.OrgMemberMapper;
import com.githubx.Github_organizations_ms.model.OrgMember;
import com.githubx.Github_organizations_ms.model.OrgMemberRole;
import com.githubx.Github_organizations_ms.model.Organization;
import com.githubx.Github_organizations_ms.model.Team;
import com.githubx.Github_organizations_ms.service.contratos.OrgMemberService;
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
public class OrgMemberServiceImpl implements OrgMemberService {

    private final OrganizationDao organizationDao;
    private final OrgMemberDao orgMemberDao;
    private final TeamDao teamDao;
    private final TeamMemberDao teamMemberDao;
    private final OrgMemberMapper orgMemberMapper;
    private final AuthenticatedUserResolver userResolver;

    @Override
    @Transactional(readOnly = true)
    public OrgMemberListResponse listOrgMembers(String orgName) {
        Organization org = findOrgOrThrow(orgName);
        assertIsMember(org.getId(), userResolver.getCurrentUserId());

        List<OrgMemberResponse> members = orgMemberDao.findAllByOrganizationId(org.getId())
                .stream()
                .map(orgMemberMapper::toResponse)
                .toList();

        return new OrgMemberListResponse(members);
    }

    @Override
    @Transactional
    public OrgMemberResponse addOrgMember(String orgName, AddOrgMemberRequest request) {
        UUID currentUserId = userResolver.getCurrentUserId();
        Organization org = findOrgOrThrow(orgName);

        // Solo el owner puede agregar miembros
        assertIsOwner(org, currentUserId);

        // Validar que no sea ya miembro
        if (orgMemberDao.existsByOrganizationIdAndUsername(org.getId(), request.username())) {
            throw EntityConflictException.memberAlreadyExists(request.username());
        }

        OrgMemberRole role = parseRole(request.role());

        // Nota: en un sistema real se consultaría el ms-usuarios para obtener el userId real
        // Aquí se usa un UUID placeholder que debe reemplazarse con la llamada gRPC/REST al ms-usuarios
        UUID newMemberUserId = UUID.randomUUID(); // TODO: resolver desde ms-usuarios

        OrgMember member = OrgMember.builder()
                .organizationId(org.getId())
                .userId(newMemberUserId)
                .username(request.username())
                .role(role)
                .build();

        member = orgMemberDao.save(member);
        log.info("Miembro agregado: {} a organización: {}", request.username(), orgName);
        return orgMemberMapper.toResponse(member);
    }

    @Override
    @Transactional
    public OrgMemberResponse updateOrgMemberRole(String orgName, String username, UpdateOrgMemberRoleRequest request) {
        UUID currentUserId = userResolver.getCurrentUserId();
        Organization org = findOrgOrThrow(orgName);

        assertIsOwner(org, currentUserId);

        OrgMember member = orgMemberDao.findByOrganizationIdAndUsername(org.getId(), username)
                .orElseThrow(() -> EntityNotFoundException.member(username));

        member.setRole(parseRole(request.role()));
        member = orgMemberDao.save(member);

        log.info("Rol actualizado para: {} en organización: {}", username, orgName);
        return orgMemberMapper.toResponse(member);
    }

    @Override
    @Transactional
    public void removeOrgMember(String orgName, String username) {
        UUID currentUserId = userResolver.getCurrentUserId();
        Organization org = findOrgOrThrow(orgName);

        assertIsOwner(org, currentUserId);

        OrgMember member = orgMemberDao.findByOrganizationIdAndUsername(org.getId(), username)
                .orElseThrow(() -> EntityNotFoundException.member(username));

        // Eliminar de todos los equipos de la organización primero
        List<UUID> orgTeamIds = teamDao.findAllByOrganizationId(org.getId())
                .stream().map(Team::getId).toList();

        if (!orgTeamIds.isEmpty()) {
            teamMemberDao.deleteByTeamIdInAndUserId(orgTeamIds, member.getUserId());
        }

        orgMemberDao.deleteByOrganizationIdAndUserId(org.getId(), member.getUserId());
        log.info("Miembro eliminado: {} de organización: {}", username, orgName);
    }

    // ===== Helpers privados =====

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

    private OrgMemberRole parseRole(String value) {
        try {
            return OrgMemberRole.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidEnumValueException("role", value, "owner, member");
        }
    }
}