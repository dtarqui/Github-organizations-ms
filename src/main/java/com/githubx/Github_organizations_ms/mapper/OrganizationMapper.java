package com.githubx.Github_organizations_ms.mapper;

import com.githubx.Github_organizations_ms.dto.response.OrganizationResponse;
import com.githubx.Github_organizations_ms.model.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {

    /**
     * Convierte una entidad Organization a su DTO de respuesta.
     * reposCount se pasa por separado desde el servicio porque viene de otro microservicio.
     */
    @Mapping(target = "visibility", expression = "java(entity.getVisibility().name().toLowerCase())")
    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAt().toString())")
    @Mapping(target = "updatedAt", expression = "java(entity.getUpdatedAt().toString())")
    @Mapping(target = "membersCount", expression = "java(entity.getMembersCount())")
    @Mapping(target = "teamsCount", expression = "java(entity.getTeamsCount())")
    @Mapping(target = "reposCount", ignore = true)
    OrganizationResponse toResponse(Organization entity);

    /**
     * Versión con reposCount inyectado externamente (viene del ms de repositorios).
     */
    default OrganizationResponse toResponse(Organization entity, int reposCount) {
        OrganizationResponse base = toResponse(entity);
        return new OrganizationResponse(
                base.id(),
                base.name(),
                base.displayName(),
                base.description(),
                base.avatarUrl(),
                base.website(),
                base.visibility(),
                base.membersCount(),
                reposCount,
                base.teamsCount(),
                base.createdAt(),
                base.updatedAt()
        );
    }
}