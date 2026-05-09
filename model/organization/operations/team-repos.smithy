$version: "2"

namespace com.minigithub.organization

use com.minigithub.common#BadRequestError
use com.minigithub.common#ForbiddenError
use com.minigithub.common#InternalServerError
use com.minigithub.common#NotFoundError
use com.minigithub.common#RepoName
use com.minigithub.common#UnauthorizedError

@http(method: "GET", uri: "/v1/orgs/{orgName}/teams/{teamId}/repos", code: 200)
@readonly
@documentation("Lista los repositorios que tiene asignados un equipo.")
operation ListTeamRepos {
    input: TeamScopeInput
    output: ListTeamReposOutput
    errors: [
        UnauthorizedError
        ForbiddenError
        NotFoundError
        InternalServerError
    ]
}

structure ListTeamReposOutput {
    @required
    @httpPayload
    body: ListTeamReposBody
}

structure ListTeamReposBody {
    @required
    repos: TeamRepoList
}

@http(method: "PUT", uri: "/v1/orgs/{orgName}/teams/{teamId}/repos/{repoName}", code: 204)
@idempotent
@documentation("Asigna un repositorio de la organización a un equipo con permisos específicos.")
operation AddTeamRepo {
    input: AddTeamRepoInput
    output: Unit
    errors: [
        BadRequestError
        UnauthorizedError
        ForbiddenError
        NotFoundError
        InternalServerError
    ]
}

structure AddTeamRepoInput {
    @required
    @httpLabel
    orgName: String

    @required
    @httpLabel
    teamId: String

    @required
    @httpLabel
    repoName: RepoName

    @required
    @httpPayload
    body: AddTeamRepoBody
}

structure AddTeamRepoBody {
    @required
    permission: TeamPermission
}

@http(method: "DELETE", uri: "/v1/orgs/{orgName}/teams/{teamId}/repos/{repoName}", code: 204)
@idempotent
@documentation("Quita el acceso de un equipo a un repositorio.")
operation RemoveTeamRepo {
    input: RemoveTeamRepoInput
    output: Unit
    errors: [
        UnauthorizedError
        ForbiddenError
        NotFoundError
        InternalServerError
    ]
}

structure RemoveTeamRepoInput {
    @required
    @httpLabel
    orgName: String

    @required
    @httpLabel
    teamId: String

    @required
    @httpLabel
    repoName: RepoName
}

@http(method: "GET", uri: "/v1/orgs/{orgName}/repos", code: 200)
@readonly
@documentation("Lista todos los repositorios que pertenecen a la organización.")
operation ListOrgRepos {
    input: ListOrgReposInput
    output: ListOrgReposOutput
    errors: [
        UnauthorizedError
        ForbiddenError
        NotFoundError
        InternalServerError
    ]
}

structure ListOrgReposInput {
    @required
    @httpLabel
    orgName: String

    @httpQuery("page")
    @range(min: 1)
    page: Integer

    @httpQuery("perPage")
    @range(min: 1, max: 50)
    perPage: Integer
}

structure ListOrgReposOutput {
    @required
    @httpPayload
    body: ListOrgReposBody
}
