package com.githubx.Github_organizations_ms.mapper;

import com.githubx.Github_organizations_ms.dto.response.TeamRepoResponse;
import com.githubx.Github_organizations_ms.model.TeamRepo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TeamRepoMapper {

    @Mapping(target = "permission", expression = "java(entity.getPermission().name().toLowerCase())")
    @Mapping(target = "assignedAt", expression = "java(entity.getAssignedAt().toString())")
    TeamRepoResponse toResponse(TeamRepo entity);
}