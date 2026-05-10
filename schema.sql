-- ============================================================
-- Github-organizations-ms | Schema PostgreSQL
-- ============================================================

-- ===== TIPOS ENUM =====

CREATE TYPE org_visibility AS ENUM ('PUBLIC', 'PRIVATE');
CREATE TYPE org_member_role AS ENUM ('OWNER', 'MEMBER', 'DEVELOPER');
CREATE TYPE team_permission AS ENUM ('READ', 'WRITE', 'ADMIN');

-- ===== TABLA: organizations =====

CREATE TABLE organizations (
    id           UUID         NOT NULL DEFAULT gen_random_uuid(),
    name         VARCHAR(50)  NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    description  TEXT,
    avatar_url   VARCHAR(500),
    website      VARCHAR(500),
    visibility   org_visibility NOT NULL DEFAULT 'PUBLIC',
    owner_id     UUID         NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT pk_organizations PRIMARY KEY (id),
    CONSTRAINT uq_org_name UNIQUE (name)
);

CREATE INDEX idx_org_owner ON organizations (owner_id);

-- ===== TABLA: org_members =====

CREATE TABLE org_members (
    organization_id UUID           NOT NULL,
    user_id         UUID           NOT NULL,
    username        VARCHAR(50)    NOT NULL,
    avatar_url      VARCHAR(500),
    role            org_member_role NOT NULL DEFAULT 'MEMBER',
    joined_at       TIMESTAMPTZ    NOT NULL DEFAULT now(),

    CONSTRAINT pk_org_members PRIMARY KEY (organization_id, user_id),
    CONSTRAINT fk_org_members_org
        FOREIGN KEY (organization_id)
        REFERENCES organizations (id)
        ON DELETE CASCADE
);

CREATE INDEX idx_org_members_username ON org_members (organization_id, username);
CREATE INDEX idx_org_members_user     ON org_members (user_id);

-- ===== TABLA: teams =====

CREATE TABLE teams (
    id              UUID          NOT NULL DEFAULT gen_random_uuid(),
    organization_id UUID          NOT NULL,
    name            VARCHAR(50)   NOT NULL,
    description     TEXT,
    permission      team_permission NOT NULL DEFAULT 'READ',
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT now(),

    CONSTRAINT pk_teams PRIMARY KEY (id),
    CONSTRAINT uq_team_name_per_org UNIQUE (organization_id, name),
    CONSTRAINT fk_teams_org
        FOREIGN KEY (organization_id)
        REFERENCES organizations (id)
        ON DELETE CASCADE
);

CREATE INDEX idx_teams_org ON teams (organization_id);

-- ===== TABLA: team_members =====

CREATE TABLE team_members (
    team_id    UUID        NOT NULL,
    user_id    UUID        NOT NULL,
    username   VARCHAR(50) NOT NULL,
    avatar_url VARCHAR(500),
    added_at   TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_team_members PRIMARY KEY (team_id, user_id),
    CONSTRAINT fk_team_members_team
        FOREIGN KEY (team_id)
        REFERENCES teams (id)
        ON DELETE CASCADE
);

CREATE INDEX idx_team_members_user ON team_members (user_id);

-- ===== TABLA: team_repos =====

CREATE TABLE team_repos (
    team_id     UUID            NOT NULL,
    repo_id     UUID            NOT NULL,
    repo_name   VARCHAR(100)    NOT NULL,
    full_name   VARCHAR(200)    NOT NULL,
    permission  team_permission NOT NULL DEFAULT 'READ',
    assigned_at TIMESTAMPTZ     NOT NULL DEFAULT now(),

    CONSTRAINT pk_team_repos PRIMARY KEY (team_id, repo_id),
    CONSTRAINT fk_team_repos_team
        FOREIGN KEY (team_id)
        REFERENCES teams (id)
        ON DELETE CASCADE
);

CREATE INDEX idx_team_repos_repo_name ON team_repos (team_id, repo_name);

-- ===== TRIGGER: actualiza updated_at en organizations automáticamente =====

CREATE OR REPLACE FUNCTION fn_set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_organizations_updated_at
    BEFORE UPDATE ON organizations
    FOR EACH ROW
    EXECUTE FUNCTION fn_set_updated_at();