package com.githubx.Github_organizations_ms.mapper;

import com.githubx.Github_organizations_ms.dto.response.OrgMemberResponse;
import com.githubx.Github_organizations_ms.model.OrgMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrgMemberMapper {

    @Mapping(target = "role", expression = "java(entity.getRole().name().toLowerCase())")
    @Mapping(target = "joinedAt", expression = "java(entity.getJoinedAt().toString())")
    OrgMemberResponse toResponse(OrgMember entity);
}