$version: "2"

namespace com.minigithub.organization

use com.minigithub.common#Url
use com.minigithub.common#Uuid
use com.minigithub.common#Username

/// Representación pública de una organización
structure OrganizationDTO {
    @required
    id: Uuid

    /// Nombre único de la organización (usado en URLs: /orgs/{orgName})
    @required
    @length(min: 3, max: 50)
    name: String

    /// Nombre descriptivo para mostrar en la UI
    @required
    @length(min: 1, max: 100)
    displayName: String

    description: String

    avatarUrl: Url

    website: Url

    @required
    visibility: OrgVisibility

    @required
    membersCount: Integer

    @required
    reposCount: Integer

    @required
    teamsCount: Integer

    @required
    createdAt: String

    @required
    updatedAt: String
}

list OrganizationList {
    member: OrganizationDTO
}

/// Miembro de una organización con su rol
structure OrgMemberDTO {
    @required
    userId: Uuid

    @required
    username: Username

    avatarUrl: Url

    @required
    role: OrgMemberRole

    @required
    joinedAt: String
}

list OrgMemberList {
    member: OrgMemberDTO
}

/// Input reutilizable para operaciones que solo necesitan orgName
structure OrgScopeInput {
    @required
    @httpLabel
    orgName: String
}
