package com.githubx.Github_organizations_ms.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.PrePersist;


import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "teams",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_team_name_per_org",
                columnNames = {"organization_id", "name"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "permission", nullable = false, length = 20)
    private TeamPermission permission;

    
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @PrePersist
public void prePersist() {
    if (this.createdAt == null) this.createdAt = Instant.now();
}


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", insertable = false, updatable = false)
    private Organization organization;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TeamMember> teamMembers = new ArrayList<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TeamRepo> teamRepos = new ArrayList<>();

    // ===== Helpers para contadores =====

    public int getMembersCount() {
        return teamMembers.size();
    }

    public int getReposCount() {
        return teamRepos.size();
    }
}