package com.githubx.Github_organizations_ms.dao;

import com.githubx.Github_organizations_ms.model.OrgMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrgMemberDao extends JpaRepository<OrgMember, OrgMember.OrgMemberId> {

    List<OrgMember> findAllByOrganizationId(UUID organizationId);

    Optional<OrgMember> findByOrganizationIdAndUsername(UUID organizationId, String username);

    Optional<OrgMember> findByOrganizationIdAndUserId(UUID organizationId, UUID userId);

    boolean existsByOrganizationIdAndUserId(UUID organizationId, UUID userId);

    boolean existsByOrganizationIdAndUsername(UUID organizationId, String username);

    void deleteByOrganizationIdAndUserId(UUID organizationId, UUID userId);

    int countByOrganizationId(UUID organizationId);
}