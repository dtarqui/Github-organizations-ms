$version: "2"

namespace com.minigithub.organization

use com.minigithub.common#BadRequestError
use com.minigithub.common#ConflictError
use com.minigithub.common#ForbiddenError
use com.minigithub.common#InternalServerError
use com.minigithub.common#NotFoundError
use com.minigithub.common#UnauthorizedError

@http(method: "GET", uri: "/v1/orgs/{orgName}/teams", code: 200)
@readonly
@documentation("Lista todos los equipos de la organización.")
operation ListOrgTeams {
    input: OrgScopeInput
    output: ListOrgTeamsOutput
    errors: [
        UnauthorizedError
        ForbiddenError
        NotFoundError
        InternalServerError
    ]
}

structure ListOrgTeamsOutput {
    @required
    @httpPayload
    body: ListOrgTeamsBody
}

structure ListOrgTeamsBody {
    @required
    teams: TeamList
}

@http(method: "POST", uri: "/v1/orgs/{orgName}/teams", code: 201)
@documentation("Crea un nuevo equipo dentro de la organización con un nivel de permisos.")
operation CreateTeam {
    input: CreateTeamInput
    output: CreateTeamOutput
    errors: [
        BadRequestError
        UnauthorizedError
        ForbiddenError
        NotFoundError
        ConflictError
        InternalServerError
    ]
}

structure CreateTeamInput {
    @required
    @httpLabel
    orgName: String

    @required
    @httpPayload
    body: CreateTeamBody
}

structure CreateTeamBody {
    @required
    @length(min: 1, max: 50)
    name: String

    description: String

    @required
    permission: TeamPermission
}

structure CreateTeamOutput {
    @required
    @httpPayload
    body: TeamDTO
}

@http(method: "GET", uri: "/v1/orgs/{orgName}/teams/{teamId}", code: 200)
@readonly
@documentation("Obtiene los datos de un equipo específico.")
operation GetTeam {
    input: TeamScopeInput
    output: GetTeamOutput
    errors: [
        UnauthorizedError
        ForbiddenError
        NotFoundError
        InternalServerError
    ]
}

structure GetTeamOutput {
    @required
    @httpPayload
    body: TeamDTO
}

@http(method: "PATCH", uri: "/v1/orgs/{orgName}/teams/{teamId}", code: 200)
@documentation("Actualiza el nombre, descripción o permisos de un equipo.")
operation UpdateTeam {
    input: UpdateTeamInput
    output: UpdateTeamOutput
    errors: [
        BadRequestError
        UnauthorizedError
        ForbiddenError
        NotFoundError
        InternalServerError
    ]
}

structure UpdateTeamInput {
    @required
    @httpLabel
    orgName: String

    @required
    @httpLabel
    teamId: String

    @required
    @httpPayload
    body: UpdateTeamBody
}

structure UpdateTeamBody {
    @length(min: 1, max: 50)
    name: String

    description: String

    permission: TeamPermission
}

structure UpdateTeamOutput {
    @required
    @httpPayload
    body: TeamDTO
}

@http(method: "DELETE", uri: "/v1/orgs/{orgName}/teams/{teamId}", code: 204)
@idempotent
@documentation("Elimina un equipo de la organización.")
operation DeleteTeam {
    input: TeamScopeInput
    output: Unit
    errors: [
        UnauthorizedError
        ForbiddenError
        NotFoundError
        InternalServerError
    ]
}
