package com.githubx.Github_organizations_ms.mapper;

import com.githubx.Github_organizations_ms.dto.response.TeamMemberResponse;
import com.githubx.Github_organizations_ms.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TeamMemberMapper {

    @Mapping(target = "addedAt", expression = "java(entity.getAddedAt().toString())")
    TeamMemberResponse toResponse(TeamMember entity);
}