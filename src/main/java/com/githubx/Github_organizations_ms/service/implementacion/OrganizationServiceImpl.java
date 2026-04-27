package com.githubx.Github_organizations_ms.service.implementacion;

import com.githubx.Github_organizations_ms.config.security.AuthenticatedUserResolver;
import com.githubx.Github_organizations_ms.dao.OrganizationDao;
import com.githubx.Github_organizations_ms.dao.OrgMemberDao;
import com.githubx.Github_organizations_ms.dto.request.CreateOrganizationRequest;
import com.githubx.Github_organizations_ms.dto.request.UpdateOrganizationRequest;
import com.githubx.Github_organizations_ms.dto.response.OrganizationPageResponse;
import com.githubx.Github_organizations_ms.dto.response.OrganizationResponse;
import com.githubx.Github_organizations_ms.dto.response.PaginationResponse;
import com.githubx.Github_organizations_ms.mapper.OrganizationMapper;
import com.githubx.Github_organizations_ms.model.OrgMember;
import com.githubx.Github_organizations_ms.model.OrgMemberRole;
import com.githubx.Github_organizations_ms.model.OrgVisibility;
import com.githubx.Github_organizations_ms.model.Organization;
import com.githubx.Github_organizations_ms.service.contratos.OrganizationService;
import com.githubx.Github_organizations_ms.util.errorhandling.EntityConflictException;
import com.githubx.Github_organizations_ms.util.errorhandling.EntityNotFoundException;
import com.githubx.Github_organizations_ms.util.errorhandling.ForbiddenOperationException;
import com.githubx.Github_organizations_ms.util.errorhandling.InvalidEnumValueException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationDao organizationDao;
    private final OrgMemberDao orgMemberDao;
    private final OrganizationMapper organizationMapper;
    private final AuthenticatedUserResolver userResolver;

    @Override
    @Transactional(readOnly = true)
    public OrganizationPageResponse listMyOrganizations(int page, int perPage) {
        UUID currentUserId = userResolver.getCurrentUserId();
        PageRequest pageRequest = PageRequest.of(page - 1, perPage);

        Page<Organization> orgPage = organizationDao.findAllByMemberUserId(currentUserId, pageRequest);

        List<OrganizationResponse> organizations = orgPage.getContent().stream()
                .map(org -> organizationMapper.toResponse(org, 0)) // reposCount: consultar ms-repos en el futuro
                .toList();

        PaginationResponse pagination = new PaginationResponse(
                page, perPage, orgPage.getTotalElements(), orgPage.getTotalPages()
        );

        return new OrganizationPageResponse(organizations, pagination);
    }

    @Override
@Transactional
public OrganizationResponse createOrganization(CreateOrganizationRequest request) {
    UUID currentUserId = userResolver.getCurrentUserId();
    String currentUsername = userResolver.getCurrentUsername();

    OrgVisibility visibility = parseVisibility(request.visibility());

    if (organizationDao.existsByName(request.name())) {
        throw EntityConflictException.organizationName(request.name());
    }

    Organization org = Organization.builder()
            .name(request.name())
            .displayName(request.displayName())
            .description(request.description())
            .website(request.website())
            .visibility(visibility)
            .ownerId(currentUserId)
            .build();

    org = organizationDao.save(org);

    OrgMember ownerMember = OrgMember.builder()
            .organizationId(org.getId())
            .userId(currentUserId)
            .username(currentUsername)
            .role(OrgMemberRole.OWNER)
            .build();

    orgMemberDao.save(ownerMember);

    // Flush para que Hibernate limpie la sesión y recargue correctamente
    organizationDao.flush();

    // Recargar la entidad fresca desde BD con timestamps populados
    org = organizationDao.findById(org.getId()).orElseThrow();

    log.info("Organización creada: {} por usuario: {}", org.getName(), currentUsername);
    return organizationMapper.toResponse(org, 0);
}

    @Override
    @Transactional(readOnly = true)
    public OrganizationResponse getOrganization(String orgName) {
        Organization org = findOrgByNameOrThrow(orgName);
        return organizationMapper.toResponse(org, 0);
    }

    @Override
    @Transactional
    public OrganizationResponse updateOrganization(String orgName, UpdateOrganizationRequest request) {
        UUID currentUserId = userResolver.getCurrentUserId();
        Organization org = findOrgByNameOrThrow(orgName);

        // Solo el owner puede actualizar
        assertIsOwner(org, currentUserId);

        // Aplicar cambios solo si el campo no es null (PATCH semántico)
        if (request.displayName() != null) org.setDisplayName(request.displayName());
        if (request.description() != null) org.setDescription(request.description());
        if (request.website() != null) org.setWebsite(request.website());
        if (request.avatarUrl() != null) org.setAvatarUrl(request.avatarUrl());
        if (request.visibility() != null) org.setVisibility(parseVisibility(request.visibility()));

        org = organizationDao.save(org);
        log.info("Organización actualizada: {}", orgName);
        return organizationMapper.toResponse(org, 0);
    }

    @Override
    @Transactional
    public void deleteOrganization(String orgName) {
        UUID currentUserId = userResolver.getCurrentUserId();
        Organization org = findOrgByNameOrThrow(orgName);

        // Solo el owner puede eliminar
        assertIsOwner(org, currentUserId);

        organizationDao.delete(org);
        log.info("Organización eliminada: {}", orgName);
    }

    // ===== Helpers privados =====

    private Organization findOrgByNameOrThrow(String orgName) {
        return organizationDao.findByName(orgName)
                .orElseThrow(() -> EntityNotFoundException.organization(orgName));
    }

    private void assertIsOwner(Organization org, UUID userId) {
        if (!org.getOwnerId().equals(userId)) {
            throw ForbiddenOperationException.notOwner();
        }
    }

    private OrgVisibility parseVisibility(String value) {
        try {
            return OrgVisibility.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidEnumValueException("visibility", value, "public, private");
        }
    }
}