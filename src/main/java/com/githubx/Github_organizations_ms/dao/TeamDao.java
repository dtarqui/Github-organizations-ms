package com.githubx.Github_organizations_ms.dao;

import com.githubx.Github_organizations_ms.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamDao extends JpaRepository<Team, UUID> {

    List<Team> findAllByOrganizationId(UUID organizationId);

    Optional<Team> findByIdAndOrganizationId(UUID teamId, UUID organizationId);

    boolean existsByOrganizationIdAndName(UUID organizationId, String name);

    int countByOrganizationId(UUID organizationId);
}