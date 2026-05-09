package com.githubx.Github_organizations_ms.controller;

import com.githubx.Github_organizations_ms.generated.model.AddOrgMemberBody;
import com.githubx.Github_organizations_ms.generated.model.ListOrgMembersBody;
import com.githubx.Github_organizations_ms.generated.model.OrgMemberDTO;
import com.githubx.Github_organizations_ms.generated.model.UpdateOrgMemberRoleBody;
import com.githubx.Github_organizations_ms.service.contratos.OrgMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/orgs/{orgName}/members")
@RequiredArgsConstructor
@Tag(name = "Org Members", description = "Gestión de miembros de organización")
@SecurityRequirement(name = "bearerAuth")
public class OrgMemberController {

    private final OrgMemberService orgMemberService;

    @GetMapping
    @Operation(summary = "Lista todos los miembros de la organización")
    public ResponseEntity<ListOrgMembersBody> listOrgMembers(
            @PathVariable String orgName) {

        return ResponseEntity.ok(orgMemberService.listOrgMembers(orgName));
    }

    @PostMapping
    @Operation(summary = "Agrega un miembro a la organización")
    public ResponseEntity<OrgMemberDTO> addOrgMember(
            @PathVariable String orgName,
            @Valid @RequestBody AddOrgMemberBody request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orgMemberService.addOrgMember(orgName, request));
    }

    @PatchMapping("/{username}")
    @Operation(summary = "Cambia el rol de un miembro")
    public ResponseEntity<OrgMemberDTO> updateOrgMemberRole(
            @PathVariable String orgName,
            @PathVariable String username,
            @Valid @RequestBody UpdateOrgMemberRoleBody request) {

        return ResponseEntity.ok(orgMemberService.updateOrgMemberRole(orgName, username, request));
    }

    @DeleteMapping("/{username}")
    @Operation(summary = "Elimina un miembro de la organización y de todos sus equipos")
    public ResponseEntity<Void> removeOrgMember(
            @PathVariable String orgName,
            @PathVariable String username) {

        orgMemberService.removeOrgMember(orgName, username);
        return ResponseEntity.noContent().build();
    }
}
