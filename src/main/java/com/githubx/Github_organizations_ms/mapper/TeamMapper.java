package com.githubx.Github_organizations_ms.mapper;

import com.githubx.Github_organizations_ms.generated.model.TeamDTO;
import com.githubx.Github_organizations_ms.model.Team;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        imports = com.githubx.Github_organizations_ms.generated.model.TeamPermission.class)
public interface TeamMapper {

    @Mapping(target = "id", expression = "java(entity.getId().toString())")
    @Mapping(target = "orgId", expression = "java(entity.getOrganizationId().toString())")
    @Mapping(target = "permission", expression = "java(TeamPermission.fromValue(entity.getPermission().name().toLowerCase()))")
    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAt().toString())")
    @Mapping(target = "membersCount", expression = "java(entity.getMembersCount())")
    @Mapping(target = "reposCount", expression = "java(entity.getReposCount())")
    TeamDTO toDto(Team entity);
}
