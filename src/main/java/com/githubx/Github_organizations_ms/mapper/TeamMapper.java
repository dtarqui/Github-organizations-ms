package com.githubx.Github_organizations_ms.mapper;

import com.githubx.Github_organizations_ms.dto.response.TeamResponse;
import com.githubx.Github_organizations_ms.model.Team;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TeamMapper {

    @Mapping(target = "orgId", source = "organizationId")
    @Mapping(target = "permission", expression = "java(entity.getPermission().name().toLowerCase())")
    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAt().toString())")
    @Mapping(target = "membersCount", expression = "java(entity.getMembersCount())")
    @Mapping(target = "reposCount", expression = "java(entity.getReposCount())")
    TeamResponse toResponse(Team entity);
}