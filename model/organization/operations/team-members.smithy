$version: "2"

namespace com.minigithub.organization

use com.minigithub.common#ConflictError
use com.minigithub.common#ForbiddenError
use com.minigithub.common#InternalServerError
use com.minigithub.common#NotFoundError
use com.minigithub.common#UnauthorizedError

@http(method: "GET", uri: "/v1/orgs/{orgName}/teams/{teamId}/members", code: 200)
@readonly
@documentation("Lista los miembros de un equipo.")
operation ListTeamMembers {
    input: TeamScopeInput
    output: ListTeamMembersOutput
    errors: [
        UnauthorizedError
        ForbiddenError
        NotFoundError
        InternalServerError
    ]
}

structure ListTeamMembersOutput {
    @required
    @httpPayload
    body: ListTeamMembersBody
}

structure ListTeamMembersBody {
    @required
    members: TeamMemberList
}

@http(method: "PUT", uri: "/v1/orgs/{orgName}/teams/{teamId}/members/{username}", code: 204)
@idempotent
@documentation("Agrega a un miembro de la organización a un equipo.")
operation AddTeamMember {
    input: TeamMemberScopeInput
    output: Unit
    errors: [
        UnauthorizedError
        ForbiddenError
        NotFoundError
        ConflictError
        InternalServerError
    ]
}

@http(method: "DELETE", uri: "/v1/orgs/{orgName}/teams/{teamId}/members/{username}", code: 204)
@idempotent
@documentation("Elimina a un miembro de un equipo (no lo elimina de la organización).")
operation RemoveTeamMember {
    input: TeamMemberScopeInput
    output: Unit
    errors: [
        UnauthorizedError
        ForbiddenError
        NotFoundError
        InternalServerError
    ]
}
