package com.githubx.Github_organizations_ms.dao;

import com.githubx.Github_organizations_ms.model.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface OrganizationDao extends JpaRepository<Organization, UUID> {

    Optional<Organization> findByName(String name);

    boolean existsByName(String name);

    /**
     * Lista las organizaciones a las que pertenece un usuario (por su userId).
     * Hace JOIN con org_members para filtrar solo las que el usuario es miembro.
     */
    @Query("""
            SELECT o FROM Organization o
            JOIN OrgMember m ON m.organizationId = o.id
            WHERE m.userId = :userId
            """)
    Page<Organization> findAllByMemberUserId(@Param("userId") UUID userId, Pageable pageable);

    /**
     * Busca organizaciones públicas por nombre o descripción (case insensitive).
     */
    @Query("""
            SELECT o FROM Organization o
            WHERE o.visibility = 'PUBLIC'
            AND (LOWER(o.name) LIKE :pattern OR LOWER(o.description) LIKE :pattern OR LOWER(o.displayName) LIKE :pattern)
            ORDER BY o.name ASC
            """)
    Page<Organization> searchByNameOrDescription(@Param("pattern") String pattern, Pageable pageable);
}