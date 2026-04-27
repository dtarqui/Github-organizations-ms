package com.githubx.Github_organizations_ms.controller;

import com.githubx.Github_organizations_ms.dto.request.CreateTeamRequest;
import com.githubx.Github_organizations_ms.dto.request.UpdateTeamRequest;
import com.githubx.Github_organizations_ms.dto.response.TeamListResponse;
import com.githubx.Github_organizations_ms.dto.response.TeamResponse;
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
    public ResponseEntity<TeamListResponse> listOrgTeams(
            @PathVariable String orgName) {

        return ResponseEntity.ok(teamService.listOrgTeams(orgName));
    }

    @PostMapping
    @Operation(summary = "Crea un nuevo equipo dentro de la organización")
    public ResponseEntity<TeamResponse> createTeam(
            @PathVariable String orgName,
            @Valid @RequestBody CreateTeamRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(teamService.createTeam(orgName, request));
    }

    @GetMapping("/{teamId}")
    @Operation(summary = "Obtiene los datos de un equipo específico")
    public ResponseEntity<TeamResponse> getTeam(
            @PathVariable String orgName,
            @PathVariable String teamId) {

        return ResponseEntity.ok(teamService.getTeam(orgName, teamId));
    }

    @PatchMapping("/{teamId}")
    @Operation(summary = "Actualiza el nombre, descripción o permisos de un equipo")
    public ResponseEntity<TeamResponse> updateTeam(
            @PathVariable String orgName,
            @PathVariable String teamId,
            @Valid @RequestBody UpdateTeamRequest request) {

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