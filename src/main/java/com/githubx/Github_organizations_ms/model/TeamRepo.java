package com.githubx.Github_organizations_ms.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.PrePersist;


import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "team_repos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(TeamRepo.TeamRepoId.class)
public class TeamRepo {

    @Id
    @Column(name = "team_id", nullable = false)
    private UUID teamId;

    @Id
    @Column(name = "repo_id", nullable = false)
    private UUID repoId;

    @Column(name = "repo_name", nullable = false, length = 100)
    private String repoName;

    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "permission", nullable = false, length = 20)
    private TeamPermission permission;

    
    @Column(name = "assigned_at", nullable = false, updatable = false)
    private Instant assignedAt;
    
@PrePersist
public void prePersist() {
    if (this.assignedAt == null) this.assignedAt = Instant.now();
}

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", insertable = false, updatable = false)
    private Team team;

    // ===== Clave compuesta =====

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class TeamRepoId implements Serializable {
        private UUID teamId;
        private UUID repoId;
    }
}