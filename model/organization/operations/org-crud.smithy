$version: "2"

namespace com.minigithub.organization

use com.minigithub.common#BadRequestError
use com.minigithub.common#ConflictError
use com.minigithub.common#ForbiddenError
use com.minigithub.common#InternalServerError
use com.minigithub.common#NotFoundError
use com.minigithub.common#PaginationMeta
use com.minigithub.common#UnauthorizedError
use com.minigithub.common#Url

@http(method: "GET", uri: "/v1/user/orgs", code: 200)
@readonly
@documentation("Lista las organizaciones a las que pertenece el usuario autenticado.")
operation ListMyOrganizations {
    input: ListMyOrganizationsInput
    output: ListMyOrganizationsOutput
    errors: [
        UnauthorizedError
        InternalServerError
    ]
}

structure ListMyOrganizationsInput {
    @httpQuery("page")
    @range(min: 1)
    page: Integer

    @httpQuery("perPage")
    @range(min: 1, max: 50)
    perPage: Integer
}

structure ListMyOrganizationsOutput {
    @required
    @httpPayload
    body: ListMyOrganizationsBody
}

structure ListMyOrganizationsBody {
    @required
    organizations: OrganizationList

    @required
    pagination: PaginationMeta
}

@http(method: "POST", uri: "/v1/orgs", code: 201)
@documentation("Crea una nueva organización. El usuario autenticado queda como owner.")
operation CreateOrganization {
    input: CreateOrganizationInput
    output: CreateOrganizationOutput
    errors: [
        BadRequestError
        UnauthorizedError
        ConflictError
        InternalServerError
    ]
}

structure CreateOrganizationInput {
    @required
    @httpPayload
    body: CreateOrganizationBody
}

structure CreateOrganizationBody {
    @required
    @length(min: 3, max: 50)
    name: String

    @required
    @length(min: 1, max: 100)
    displayName: String

    description: String

    website: Url

    @required
    visibility: OrgVisibility
}

structure CreateOrganizationOutput {
    @required
    @httpPayload
    body: OrganizationDTO
}

@http(method: "GET", uri: "/v1/orgs/{orgName}", code: 200)
@readonly
@documentation("Obtiene los datos públicos de una organización.")
operation GetOrganization {
    input: GetOrganizationInput
    output: GetOrganizationOutput
    errors: [
        UnauthorizedError
        NotFoundError
        InternalServerError
    ]
}

structure GetOrganizationInput {
    @required
    @httpLabel
    orgName: String
}

structure GetOrganizationOutput {
    @required
    @httpPayload
    body: OrganizationDTO
}

@http(method: "PATCH", uri: "/v1/orgs/{orgName}", code: 200)
@documentation("Actualiza los datos de la organización. Solo el owner puede hacerlo.")
operation UpdateOrganization {
    input: UpdateOrganizationInput
    output: UpdateOrganizationOutput
    errors: [
        BadRequestError
        UnauthorizedError
        ForbiddenError
        NotFoundError
        InternalServerError
    ]
}

structure UpdateOrganizationInput {
    @required
    @httpLabel
    orgName: String

    @required
    @httpPayload
    body: UpdateOrganizationBody
}

structure UpdateOrganizationBody {
    @length(min: 1, max: 100)
    displayName: String

    description: String

    website: Url

    avatarUrl: Url

    visibility: OrgVisibility
}

structure UpdateOrganizationOutput {
    @required
    @httpPayload
    body: OrganizationDTO
}

@http(method: "DELETE", uri: "/v1/orgs/{orgName}", code: 204)
@idempotent
@documentation("Elimina la organización y todos sus equipos y repositorios. Solo el owner puede hacerlo.")
operation DeleteOrganization {
    input: DeleteOrganizationInput
    output: Unit
    errors: [
        UnauthorizedError
        ForbiddenError
        NotFoundError
        InternalServerError
    ]
}

structure DeleteOrganizationInput {
    @required
    @httpLabel
    orgName: String
}
