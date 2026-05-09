package com.githubx.Github_organizations_ms.mapper;

import com.githubx.Github_organizations_ms.generated.model.OrganizationDTO;
import com.githubx.Github_organizations_ms.model.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        imports = com.githubx.Github_organizations_ms.generated.model.OrgVisibility.class)
public interface OrganizationMapper {

    @Mapping(target = "id", expression = "java(entity.getId().toString())")
    @Mapping(target = "visibility", expression = "java(OrgVisibility.fromValue(entity.getVisibility().name().toLowerCase()))")
    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAt().toString())")
    @Mapping(target = "updatedAt", expression = "java(entity.getUpdatedAt().toString())")
    @Mapping(target = "membersCount", expression = "java(entity.getMembersCount())")
    @Mapping(target = "teamsCount", expression = "java(entity.getTeamsCount())")
    @Mapping(target = "reposCount", ignore = true)
    OrganizationDTO toDto(Organization entity);

    /**
     * reposCount se inyecta externamente porque viene del microservicio de repositorios.
     */
    default OrganizationDTO toDto(Organization entity, int reposCount) {
        OrganizationDTO dto = toDto(entity);
        dto.setReposCount(reposCount);
        return dto;
    }
}
