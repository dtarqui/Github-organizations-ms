package com.githubx.Github_organizations_ms.service.implementacion;

import com.githubx.Github_organizations_ms.config.security.AuthenticatedUserResolver;
import com.githubx.Github_organizations_ms.dao.OrganizationDao;
import com.githubx.Github_organizations_ms.dao.OrgMemberDao;
import com.githubx.Github_organizations_ms.generated.model.CreateOrganizationBody;
import com.githubx.Github_organizations_ms.generated.model.ListMyOrganizationsBody;
import com.githubx.Github_organizations_ms.generated.model.OrganizationDTO;
import com.githubx.Github_organizations_ms.generated.model.PaginationMeta;
import com.githubx.Github_organizations_ms.generated.model.SearchOrganizationsBody;
import com.githubx.Github_organizations_ms.generated.model.UpdateOrganizationBody;
import com.githubx.Github_organizations_ms.mapper.OrganizationMapper;
import com.githubx.Github_organizations_ms.model.OrgMember;
import com.githubx.Github_organizations_ms.model.OrgMemberRole;
import com.githubx.Github_organizations_ms.model.OrgVisibility;
import com.githubx.Github_organizations_ms.model.Organization;
import com.githubx.Github_organizations_ms.service.contratos.OrganizationService;
import com.githubx.Github_organizations_ms.util.errorhandling.EntityConflictException;
import com.githubx.Github_organizations_ms.util.errorhandling.EntityNotFoundException;
import com.githubx.Github_organizations_ms.util.errorhandling.ForbiddenOperationException;
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
    public ListMyOrganizationsBody listMyOrganizations(int page, int perPage) {
        UUID currentUserId = userResolver.getCurrentUserId();
        PageRequest pageRequest = PageRequest.of(page - 1, perPage);

        Page<Organization> orgPage = organizationDao.findAllByMemberUserId(currentUserId, pageRequest);

        List<OrganizationDTO> organizations = orgPage.getContent().stream()
                .map(org -> organizationMapper.toDto(org, 0))
                .toList();

        PaginationMeta pagination = new PaginationMeta()
                .page(page)
                .perPage(perPage)
                .total((int) orgPage.getTotalElements())
                .totalPages(orgPage.getTotalPages());

        return new ListMyOrganizationsBody()
                .organizations(organizations)
                .pagination(pagination);
    }

    @Override
    @Transactional
    public OrganizationDTO createOrganization(CreateOrganizationBody request) {
        UUID currentUserId = userResolver.getCurrentUserId();
        String currentUsername = userResolver.getCurrentUsername();

        OrgVisibility visibility = OrgVisibility.valueOf(request.getVisibility().name());

        if (organizationDao.existsByName(request.getName())) {
            throw EntityConflictException.organizationName(request.getName());
        }

        Organization org = Organization.builder()
                .name(request.getName())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .website(request.getWebsite())
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

        organizationDao.flush();
        org = organizationDao.findById(org.getId()).orElseThrow();

        log.info("Organización creada: {} por usuario: {}", org.getName(), currentUsername);
        return organizationMapper.toDto(org, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public OrganizationDTO getOrganization(String orgName) {
        Organization org = findOrgByNameOrThrow(orgName);
        return organizationMapper.toDto(org, 0);
    }

    @Override
    @Transactional
    public OrganizationDTO updateOrganization(String orgName, UpdateOrganizationBody request) {
        UUID currentUserId = userResolver.getCurrentUserId();
        Organization org = findOrgByNameOrThrow(orgName);

        assertIsOwner(org, currentUserId);

        if (request.getDisplayName() != null) org.setDisplayName(request.getDisplayName());
        if (request.getDescription() != null) org.setDescription(request.getDescription());
        if (request.getWebsite() != null) org.setWebsite(request.getWebsite());
        if (request.getAvatarUrl() != null) org.setAvatarUrl(request.getAvatarUrl());
        if (request.getVisibility() != null)
            org.setVisibility(OrgVisibility.valueOf(request.getVisibility().name()));

        org = organizationDao.save(org);
        log.info("Organización actualizada: {}", orgName);
        return organizationMapper.toDto(org, 0);
    }

    @Override
    @Transactional
    public void deleteOrganization(String orgName) {
        UUID currentUserId = userResolver.getCurrentUserId();
        Organization org = findOrgByNameOrThrow(orgName);

        assertIsOwner(org, currentUserId);

        organizationDao.delete(org);
        log.info("Organización eliminada: {}", orgName);
    }

    private Organization findOrgByNameOrThrow(String orgName) {
        return organizationDao.findByName(orgName)
                .orElseThrow(() -> EntityNotFoundException.organization(orgName));
    }

    private void assertIsOwner(Organization org, UUID userId) {
        if (!org.getOwnerId().equals(userId)) {
            throw ForbiddenOperationException.notOwner();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public SearchOrganizationsBody searchOrganizations(String query, int page, int perPage) {
        PageRequest pageRequest = PageRequest.of(page - 1, perPage);
        String searchPattern = "%" + query.toLowerCase() + "%";

        Page<Organization> orgPage = organizationDao.searchByNameOrDescription(searchPattern, pageRequest);

        List<OrganizationDTO> organizations = orgPage.getContent().stream()
                .map(org -> organizationMapper.toDto(org, 0))
                .toList();

        PaginationMeta pagination = new PaginationMeta()
                .page(page)
                .perPage(perPage)
                .total((int) orgPage.getTotalElements())
                .totalPages(orgPage.getTotalPages());

        return new SearchOrganizationsBody()
                .organizations(organizations)
                .pagination(pagination);
    }
}
