package com.githubx.Github_organizations_ms.service;

import com.githubx.Github_organizations_ms.config.security.AuthenticatedUserResolver;
import com.githubx.Github_organizations_ms.dao.OrgMemberDao;
import com.githubx.Github_organizations_ms.dao.OrganizationDao;
import com.githubx.Github_organizations_ms.generated.model.CreateOrganizationBody;
import com.githubx.Github_organizations_ms.generated.model.OrganizationDTO;
import com.githubx.Github_organizations_ms.generated.model.OrgVisibility;
import com.githubx.Github_organizations_ms.mapper.OrganizationMapper;
import com.githubx.Github_organizations_ms.model.OrgMember;
import com.githubx.Github_organizations_ms.model.Organization;
import com.githubx.Github_organizations_ms.service.implementacion.OrganizationServiceImpl;
import com.githubx.Github_organizations_ms.util.errorhandling.EntityConflictException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {

    @Mock
    private OrganizationDao organizationDao;

    @Mock
    private OrgMemberDao orgMemberDao;

    @Mock
    private OrganizationMapper organizationMapper;

    @Mock
    private AuthenticatedUserResolver userResolver;

    @InjectMocks
    private OrganizationServiceImpl organizationService;

    @Test
    void debeCrearOrganizacionExitosamente() {
        UUID userId = UUID.randomUUID();
        when(userResolver.getCurrentUserId()).thenReturn(userId);
        when(userResolver.getCurrentUsername()).thenReturn("testuser");
        when(organizationDao.existsByName("acme-org")).thenReturn(false);

        Organization savedOrg = Organization.builder()
                .id(UUID.randomUUID())
                .name("acme-org")
                .displayName("Acme Org")
                .visibility(com.githubx.Github_organizations_ms.model.OrgVisibility.PUBLIC)
                .ownerId(userId)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(organizationDao.save(any(Organization.class))).thenReturn(savedOrg);
        when(orgMemberDao.save(any(OrgMember.class))).thenReturn(null);
        when(organizationDao.findById(any(UUID.class))).thenReturn(Optional.of(savedOrg));

        OrganizationDTO expectedResponse = new OrganizationDTO()
                .id(savedOrg.getId().toString())
                .name("acme-org")
                .displayName("Acme Org")
                .visibility(OrgVisibility.PUBLIC)
                .membersCount(1)
                .reposCount(0)
                .teamsCount(0)
                .createdAt(Instant.now().toString())
                .updatedAt(Instant.now().toString());

        when(organizationMapper.toDto(any(Organization.class), eq(0))).thenReturn(expectedResponse);

        CreateOrganizationBody request = new CreateOrganizationBody()
                .name("acme-org")
                .displayName("Acme Org")
                .visibility(OrgVisibility.PUBLIC);

        OrganizationDTO result = organizationService.createOrganization(request);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("acme-org");
        verify(organizationDao, times(1)).save(any(Organization.class));
    }

    @Test
    void debeLanzarConflictSiNombreYaExiste() {
        when(userResolver.getCurrentUserId()).thenReturn(UUID.randomUUID());
        when(userResolver.getCurrentUsername()).thenReturn("testuser");
        when(organizationDao.existsByName("acme-org")).thenReturn(true);

        CreateOrganizationBody request = new CreateOrganizationBody()
                .name("acme-org")
                .displayName("Acme Org")
                .visibility(OrgVisibility.PUBLIC);

        assertThatThrownBy(() -> organizationService.createOrganization(request))
                .isInstanceOf(EntityConflictException.class)
                .hasMessageContaining("acme-org");
    }
}
