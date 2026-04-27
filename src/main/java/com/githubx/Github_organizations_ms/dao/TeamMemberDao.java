package com.githubx.Github_organizations_ms.dao;

import com.githubx.Github_organizations_ms.model.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamMemberDao extends JpaRepository<TeamMember, TeamMember.TeamMemberId> {

    List<TeamMember> findAllByTeamId(UUID teamId);

    Optional<TeamMember> findByTeamIdAndUserId(UUID teamId, UUID userId);

    boolean existsByTeamIdAndUserId(UUID teamId, UUID userId);

    void deleteByTeamIdAndUserId(UUID teamId, UUID userId);

    /**
     * Elimina a un usuario de TODOS los equipos de una organización.
     * Se usa cuando se expulsa a un miembro de la organización completa.
     */
    void deleteByTeamIdInAndUserId(List<UUID> teamIds, UUID userId);

    int countByTeamId(UUID teamId);
}