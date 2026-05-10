package com.githubx.Github_organizations_ms.controller;

import com.githubx.Github_organizations_ms.generated.model.CreateTeamBody;
import com.githubx.Github_organizations_ms.generated.model.ListOrgTeamsBody;
import com.githubx.Github_organizations_ms.generated.model.TeamDTO;
import com.githubx.Github_organizations_ms.generated.model.UpdateTeamBody;
import com.githubx.Github_organizations_ms.service.contratos.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/orgs/{orgName}/teams")
@RequiredArgsConstructor
@Tag(name = "Teams", description = "Gestión de equipos de una organización")
@SecurityRequirement(name = "bearerAuth")
public class TeamController {

    private final TeamService teamService;

    @GetMapping
    @Operation(summary = "Lista todos los equipos de la organización")
    public ResponseEntity<ListOrgTeamsBody> listOrgTeams(
            @PathVariable String orgName) {

        return ResponseEntity.ok(teamService.listOrgTeams(orgName));
    }

    @PostMapping
    @Operation(summary = "Crea un nuevo equipo dentro de la organización")
    public ResponseEntity<TeamDTO> createTeam(
            @PathVariable String orgName,
            @Valid @RequestBody CreateTeamBody request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(teamService.createTeam(orgName, request));
    }

    @GetMapping("/{teamId}")
    @Operation(summary = "Obtiene los datos de un equipo específico")
    public ResponseEntity<TeamDTO> getTeam(
            @PathVariable String orgName,
            @PathVariable String teamId) {

        return ResponseEntity.ok(teamService.getTeam(orgName, teamId));
    }

    @PatchMapping("/{teamId}")
    @Operation(summary = "Actualiza el nombre, descripción o permisos de un equipo")
    public ResponseEntity<TeamDTO> updateTeam(
            @PathVariable String orgName,
            @PathVariable String teamId,
            @Valid @RequestBody UpdateTeamBody request) {

        return ResponseEntity.ok(teamService.updateTeam(orgName, teamId, request));
    }

    @DeleteMapping("/{teamId}")
    @Operation(summary = "Elimina un equipo de la organización")
    public ResponseEntity<Void> deleteTeam(
            @PathVariable String orgName,
            @PathVariable String teamId) {

        teamService.deleteTeam(orgName, teamId);
        return ResponseEntity.noContent().build();
    }
}
