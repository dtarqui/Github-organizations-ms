package com.githubx.Github_organizations_ms.model;

import jakarta.persistence.*;
import lombok.*;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "organizations",
        uniqueConstraints = @UniqueConstraint(name = "uq_org_name", columnNames = "name"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "website", length = 500)
    private String website;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false, length = 20)
    private OrgVisibility visibility;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    
       @PrePersist
public void prePersist() {
    Instant now = Instant.now();
    if (this.createdAt == null) this.createdAt = now;
    if (this.updatedAt == null) this.updatedAt = now;
}

@PreUpdate
public void preUpdate() {
    this.updatedAt = Instant.now();
}
 

    // ===== Relaciones =====

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrgMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Team> teams = new ArrayList<>();

    // ===== Helpers para contadores =====

    public int getMembersCount() {
        return members.size();
    }

    public int getTeamsCount() {
        return teams.size();
    }
}