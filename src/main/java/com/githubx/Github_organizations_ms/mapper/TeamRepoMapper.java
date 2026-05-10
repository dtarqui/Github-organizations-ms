package com.githubx.Github_organizations_ms.mapper;

import com.githubx.Github_organizations_ms.generated.model.TeamRepoDTO;
import com.githubx.Github_organizations_ms.model.TeamRepo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        imports = com.githubx.Github_organizations_ms.generated.model.TeamPermission.class)
public interface TeamRepoMapper {

    @Mapping(target = "repoId", expression = "java(entity.getRepoId().toString())")
    @Mapping(target = "permission", expression = "java(TeamPermission.fromValue(entity.getPermission().name().toLowerCase()))")
    @Mapping(target = "assignedAt", expression = "java(entity.getAssignedAt().toString())")
    TeamRepoDTO toDto(TeamRepo entity);
}
