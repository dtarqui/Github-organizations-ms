package com.githubx.Github_organizations_ms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepoAccessResponse {
    private List<TeamAccessDTO> teams;
    private List<CollaboratorDTO> collaborators;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamAccessDTO {
        private String teamId;
        private String teamName;
        private String orgName;
        private String permission;
        private List<TeamMemberDTO> members;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamMemberDTO {
        private String userId;
        private String username;
        private String avatarUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CollaboratorDTO {
        private String userId;
        private String username;
        private String avatarUrl;
        private String role;
    }
}
