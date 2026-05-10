$version: "2"

namespace com.minigithub.organization

use aws.protocols#restJson1

@title("Mini-GitHub Organization Public API")
@restJson1
@documentation("Operaciones públicas de organización (solo lectura sin token).")
service OrgPublicApi {
    version: "1.0.0"
    operations: [
        GetOrganization
        ListOrgRepos
    ]
}

@title("Mini-GitHub Organization API")
@restJson1
@httpBearerAuth
@documentation("Operaciones de organización protegidas. Requiere token JWT.")
service OrgApi {
    version: "1.0.0"
    operations: [
        // Organizaciones
        ListMyOrganizations
        CreateOrganization
        UpdateOrganization
        DeleteOrganization

        // Miembros de la organización
        ListOrgMembers
        AddOrgMember
        UpdateOrgMemberRole
        RemoveOrgMember

        // Equipos
        ListOrgTeams
        CreateTeam
        GetTeam
        UpdateTeam
        DeleteTeam

        // Miembros del equipo
        ListTeamMembers
        AddTeamMember
        RemoveTeamMember

        // Repositorios del equipo
        ListTeamRepos
        AddTeamRepo
        RemoveTeamRepo

        // Repositorios de la organización
        ListOrgRepos
    ]
}
