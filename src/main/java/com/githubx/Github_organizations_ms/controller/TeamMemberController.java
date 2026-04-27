package com.githubx.Github_organizations_ms.controller;

import com.githubx.Github_organizations_ms.dto.response.TeamMemberListResponse;
import com.githubx.Github_organizations_ms.service.contratos.TeamMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/orgs/{orgName}/teams/{teamId}/members")
@RequiredArgsConstructor
@Tag(name = "Team Members", description = "Gestión de miembros de equipo")
@SecurityRequirement(name = "bearerAuth")
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    @GetMapping
    @Operation(summary = "Lista los miembros de un equipo")
    public ResponseEntity<TeamMemberListResponse> listTeamMembers(
            @PathVariable String orgName,
            @PathVariable String teamId) {

        return ResponseEntity.ok(teamMemberService.listTeamMembers(orgName, teamId));
    }

    @PutMapping("/{username}")
    @Operation(summary = "Agrega a un miembro de la organización a un equipo")
    public ResponseEntity<Void> addTeamMember(
            @PathVariable String orgName,
            @PathVariable String teamId,
            @PathVariable String username) {

        teamMemberService.addTeamMember(orgName, teamId, username);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{username}")
    @Operation(summary = "Elimina a un miembro de un equipo")
    public ResponseEntity<Void> removeTeamMember(
            @PathVariable String orgName,
            @PathVariable String teamId,
            @PathVariable String username) {

        teamMemberService.removeTeamMember(orgName, teamId, username);
        return ResponseEntity.noContent().build();
    }
}