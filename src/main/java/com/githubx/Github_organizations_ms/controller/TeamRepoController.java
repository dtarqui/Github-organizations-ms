package com.githubx.Github_organizations_ms.controller;

import com.githubx.Github_organizations_ms.generated.model.AddTeamRepoBody;
import com.githubx.Github_organizations_ms.generated.model.ListOrgReposBody;
import com.githubx.Github_organizations_ms.generated.model.ListTeamReposBody;
import com.githubx.Github_organizations_ms.service.contratos.TeamRepoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Team Repos", description = "Gestión de repositorios de equipo y organización")
public class TeamRepoController {

    private final TeamRepoService teamRepoService;

    @GetMapping("/v1/orgs/{orgName}/teams/{teamId}/repos")
    @Operation(summary = "Lista los repositorios asignados a un equipo",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ListTeamReposBody> listTeamRepos(
            @PathVariable String orgName,
            @PathVariable String teamId) {

        return ResponseEntity.ok(teamRepoService.listTeamRepos(orgName, teamId));
    }

    @PutMapping("/v1/orgs/{orgName}/teams/{teamId}/repos/{repoName}")
    @Operation(summary = "Asigna un repositorio a un equipo con permisos específicos",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> addTeamRepo(
            @PathVariable String orgName,
            @PathVariable String teamId,
            @PathVariable String repoName,
            @Valid @RequestBody AddTeamRepoBody request) {

        teamRepoService.addTeamRepo(orgName, teamId, repoName, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/v1/orgs/{orgName}/teams/{teamId}/repos/{repoName}")
    @Operation(summary = "Quita el acceso de un equipo a un repositorio",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> removeTeamRepo(
            @PathVariable String orgName,
            @PathVariable String teamId,
            @PathVariable String repoName) {

        teamRepoService.removeTeamRepo(orgName, teamId, repoName);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/v1/orgs/{orgName}/repos")
    @Operation(summary = "Lista todos los repositorios de la organización")
    public ResponseEntity<ListOrgReposBody> listOrgRepos(
            @PathVariable String orgName,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int perPage) {

        return ResponseEntity.ok(teamRepoService.listOrgRepos(orgName, page, perPage));
    }
}
