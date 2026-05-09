package com.githubx.Github_organizations_ms.mapper;

import com.githubx.Github_organizations_ms.generated.model.OrgMemberDTO;
import com.githubx.Github_organizations_ms.model.OrgMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        imports = com.githubx.Github_organizations_ms.generated.model.OrgMemberRole.class)
public interface OrgMemberMapper {

    @Mapping(target = "userId", expression = "java(entity.getUserId().toString())")
    @Mapping(target = "role", expression = "java(OrgMemberRole.fromValue(entity.getRole().name().toLowerCase()))")
    @Mapping(target = "joinedAt", expression = "java(entity.getJoinedAt().toString())")
    OrgMemberDTO toDto(OrgMember entity);
}
