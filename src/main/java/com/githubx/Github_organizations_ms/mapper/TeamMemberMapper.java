package com.githubx.Github_organizations_ms.mapper;

import com.githubx.Github_organizations_ms.generated.model.TeamMemberDTO;
import com.githubx.Github_organizations_ms.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TeamMemberMapper {

    @Mapping(target = "userId", expression = "java(entity.getUserId().toString())")
    @Mapping(target = "addedAt", expression = "java(entity.getAddedAt().toString())")
    TeamMemberDTO toDto(TeamMember entity);
}
