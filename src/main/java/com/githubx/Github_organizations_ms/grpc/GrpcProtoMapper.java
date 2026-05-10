package com.githubx.Github_organizations_ms.grpc;

import com.githubx.Github_organizations_ms.generated.model.OrgMemberRole;
import com.githubx.Github_organizations_ms.generated.model.OrgVisibility;
import com.githubx.Github_organizations_ms.generated.model.TeamPermission;
import com.minigithub.grpc.proto.*;
import org.springframework.stereotype.Component;

@Component
public class GrpcProtoMapper {

    // ─── OrgVisibility ────────────────────────────────────────

    public com.minigithub.grpc.proto.OrgVisibility toProtoVisibility(OrgVisibility v) {
        if (v == null) return com.minigithub.grpc.proto.OrgVisibility.ORG_VISIBILITY_UNSPECIFIED;
        return switch (v) {
            case PUBLIC -> com.minigithub.grpc.proto.OrgVisibility.ORG_VISIBILITY_PUBLIC;
            case PRIVATE -> com.minigithub.grpc.proto.OrgVisibility.ORG_VISIBILITY_PRIVATE;
        };
    }

    public OrgVisibility fromProtoVisibility(com.minigithub.grpc.proto.OrgVisibility v) {
        return switch (v) {
            case ORG_VISIBILITY_PUBLIC -> OrgVisibility.PUBLIC;
            case ORG_VISIBILITY_PRIVATE -> OrgVisibility.PRIVATE;
            default -> OrgVisibility.PUBLIC;
        };
    }

    // ─── OrgMemberRole ────────────────────────────────────────

    public com.minigithub.grpc.proto.OrgMemberRole toProtoMemberRole(OrgMemberRole r) {
        if (r == null) return com.minigithub.grpc.proto.OrgMemberRole.ORG_MEMBER_ROLE_UNSPECIFIED;
        return switch (r) {
            case OWNER -> com.minigithub.grpc.proto.OrgMemberRole.ORG_MEMBER_ROLE_OWNER;
            case MEMBER, DEVELOPER -> com.minigithub.grpc.proto.OrgMemberRole.ORG_MEMBER_ROLE_MEMBER;
        };
    }

    public OrgMemberRole fromProtoMemberRole(com.minigithub.grpc.proto.OrgMemberRole r) {
        return switch (r) {
            case ORG_MEMBER_ROLE_OWNER -> OrgMemberRole.OWNER;
            case ORG_MEMBER_ROLE_MEMBER -> OrgMemberRole.MEMBER;
            default -> OrgMemberRole.MEMBER;
        };
    }

    // ─── TeamPermission ───────────────────────────────────────

    public com.minigithub.grpc.proto.TeamPermission toProtoPermission(TeamPermission p) {
        if (p == null) return com.minigithub.grpc.proto.TeamPermission.TEAM_PERMISSION_UNSPECIFIED;
        return switch (p) {
            case READ -> com.minigithub.grpc.proto.TeamPermission.TEAM_PERMISSION_READ;
            case WRITE -> com.minigithub.grpc.proto.TeamPermission.TEAM_PERMISSION_WRITE;
            case ADMIN -> com.minigithub.grpc.proto.TeamPermission.TEAM_PERMISSION_ADMIN;
        };
    }

    public TeamPermission fromProtoPermission(com.minigithub.grpc.proto.TeamPermission p) {
        return switch (p) {
            case TEAM_PERMISSION_READ -> TeamPermission.READ;
            case TEAM_PERMISSION_WRITE -> TeamPermission.WRITE;
            case TEAM_PERMISSION_ADMIN -> TeamPermission.ADMIN;
            default -> TeamPermission.READ;
        };
    }

    // ─── OrganizationDTO ──────────────────────────────────────

    public OrganizationDTO toProtoOrg(com.githubx.Github_organizations_ms.generated.model.OrganizationDTO dto) {
        return OrganizationDTO.newBuilder()
                .setId(safe(dto.getId()))
                .setName(safe(dto.getName()))
                .setDisplayName(safe(dto.getDisplayName()))
                .setDescription(safe(dto.getDescription()))
                .setAvatarUrl(safe(dto.getAvatarUrl()))
                .setWebsite(safe(dto.getWebsite()))
                .setVisibility(toProtoVisibility(dto.getVisibility()))
                .setMembersCount(safeInt(dto.getMembersCount()))
                .setReposCount(safeInt(dto.getReposCount()))
                .setTeamsCount(safeInt(dto.getTeamsCount()))
                .setCreatedAt(safe(dto.getCreatedAt()))
                .setUpdatedAt(safe(dto.getUpdatedAt()))
                .build();
    }

    // ─── PaginationMeta ───────────────────────────────────────

    public PaginationMeta toProtoPagination(com.githubx.Github_organizations_ms.generated.model.PaginationMeta m) {
        return PaginationMeta.newBuilder()
                .setPage(safeInt(m.getPage()))
                .setPerPage(safeInt(m.getPerPage()))
                .setTotal(safeInt(m.getTotal()))
                .setTotalPages(safeInt(m.getTotalPages()))
                .build();
    }

    // ─── OrgMemberDTO ─────────────────────────────────────────

    public OrgMemberDTO toProtoMember(com.githubx.Github_organizations_ms.generated.model.OrgMemberDTO dto) {
        return OrgMemberDTO.newBuilder()
                .setUserId(safe(dto.getUserId()))
                .setUsername(safe(dto.getUsername()))
                .setAvatarUrl(safe(dto.getAvatarUrl()))
                .setRole(toProtoMemberRole(dto.getRole()))
                .setJoinedAt(safe(dto.getJoinedAt()))
                .build();
    }

    // ─── TeamDTO ──────────────────────────────────────────────

    public TeamDTO toProtoTeam(com.githubx.Github_organizations_ms.generated.model.TeamDTO dto) {
        return TeamDTO.newBuilder()
                .setId(safe(dto.getId()))
                .setOrgId(safe(dto.getOrgId()))
                .setName(safe(dto.getName()))
                .setDescription(safe(dto.getDescription()))
                .setPermission(toProtoPermission(dto.getPermission()))
                .setMembersCount(safeInt(dto.getMembersCount()))
                .setReposCount(safeInt(dto.getReposCount()))
                .setCreatedAt(safe(dto.getCreatedAt()))
                .build();
    }

    // ─── TeamMemberDTO ────────────────────────────────────────

    public TeamMemberDTO toProtoTeamMember(com.githubx.Github_organizations_ms.generated.model.TeamMemberDTO dto) {
        return TeamMemberDTO.newBuilder()
                .setUserId(safe(dto.getUserId()))
                .setUsername(safe(dto.getUsername()))
                .setAvatarUrl(safe(dto.getAvatarUrl()))
                .setAddedAt(safe(dto.getAddedAt()))
                .build();
    }

    // ─── TeamRepoDTO ──────────────────────────────────────────

    public TeamRepoDTO toProtoTeamRepo(com.githubx.Github_organizations_ms.generated.model.TeamRepoDTO dto) {
        return TeamRepoDTO.newBuilder()
                .setRepoId(safe(dto.getRepoId()))
                .setRepoName(safe(dto.getRepoName()))
                .setFullName(safe(dto.getFullName()))
                .setPermission(toProtoPermission(dto.getPermission()))
                .setAssignedAt(safe(dto.getAssignedAt()))
                .build();
    }

    // ─── OrgRepoSummary ───────────────────────────────────────

    public OrgRepoSummary toProtoRepoSummary(com.githubx.Github_organizations_ms.generated.model.OrgRepoSummary dto) {
        return OrgRepoSummary.newBuilder()
                .setId(safe(dto.getId()))
                .setName(safe(dto.getName()))
                .setFullName(safe(dto.getFullName()))
                .setDescription(safe(dto.getDescription()))
                .setStarsCount(safeInt(dto.getStarsCount()))
                .setUpdatedAt(safe(dto.getUpdatedAt()))
                .build();
    }

    // ─── Helpers ──────────────────────────────────────────────

    private String safe(String s) {
        return s != null ? s : "";
    }

    private int safeInt(Integer i) {
        return i != null ? i : 0;
    }
}
