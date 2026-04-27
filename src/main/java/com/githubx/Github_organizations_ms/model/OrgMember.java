package com.githubx.Github_organizations_ms.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.PrePersist;


import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "org_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(OrgMember.OrgMemberId.class)
public class OrgMember {

    @Id
    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Id
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private OrgMemberRole role;

    
    @Column(name = "joined_at", nullable = false, updatable = false)
    private Instant joinedAt;
    @PrePersist
public void prePersist() {
    if (this.joinedAt == null) this.joinedAt = Instant.now();
}

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", insertable = false, updatable = false)
    private Organization organization;

    // ===== Clave compuesta =====

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class OrgMemberId implements Serializable {
        private UUID organizationId;
        private UUID userId;
    }
}