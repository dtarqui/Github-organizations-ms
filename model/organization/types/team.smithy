$version: "2"

namespace com.minigithub.organization

use com.minigithub.common#RepoName
use com.minigithub.common#Username
use com.minigithub.common#Uuid

/// Un equipo agrupa miembros y define sus permisos sobre repositorios
structure TeamDTO {
    @required
    id: Uuid

    @required
    orgId: Uuid

    @required
    @length(min: 1, max: 50)
    name: String

    description: String

    @required
    permission: TeamPermission

    @required
    membersCount: Integer

    @required
    reposCount: Integer

    @required
    createdAt: String
}

list TeamList {
    member: TeamDTO
}

/// Miembro de un equipo
structure TeamMemberDTO {
    @required
    userId: Uuid

    @required
    username: Username

    avatarUrl: String

    @required
    addedAt: String
}

list TeamMemberList {
    member: TeamMemberDTO
}

/// Repositorio asignado a un equipo
structure TeamRepoDTO {
    @required
    repoId: Uuid

    @required
    repoName: RepoName

    @required
    fullName: String

    @required
    permission: TeamPermission

    @required
    assignedAt: String
}

list TeamRepoList {
    member: TeamRepoDTO
}

/// Input reutilizable para operaciones de equipo
structure TeamScopeInput {
    @required
    @httpLabel
    orgName: String

    @required
    @httpLabel
    teamId: String
}

/// Input reutilizable para operaciones de miembro de equipo
structure TeamMemberScopeInput {
    @required
    @httpLabel
    orgName: String

    @required
    @httpLabel
    teamId: String

    @required
    @httpLabel
    username: Username
}
