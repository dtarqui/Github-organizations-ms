package com.githubx.Github_organizations_ms.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.PrePersist;


import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "team_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(TeamMember.TeamMemberId.class)
public class TeamMember {

    @Id
    @Column(name = "team_id", nullable = false)
    private UUID teamId;

    @Id
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    
    @Column(name = "added_at", nullable = false, updatable = false)
    private Instant addedAt;
@PrePersist
public void prePersist() {
    if (this.addedAt == null) this.addedAt = Instant.now();
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
    public static class TeamMemberId implements Serializable {
        private UUID teamId;
        private UUID userId;
    }
}