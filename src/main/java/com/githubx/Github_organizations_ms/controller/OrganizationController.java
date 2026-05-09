package com.githubx.Github_organizations_ms.controller;

import com.githubx.Github_organizations_ms.generated.model.CreateOrganizationBody;
import com.githubx.Github_organizations_ms.generated.model.ListMyOrganizationsBody;
import com.githubx.Github_organizations_ms.generated.model.OrganizationDTO;
import com.githubx.Github_organizations_ms.generated.model.UpdateOrganizationBody;
import com.githubx.Github_organizations_ms.service.contratos.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Organizations", description = "Gestión de organizaciones")
public class OrganizationController {

    private final OrganizationService organizationService;

    @GetMapping("/v1/user/orgs")
    @Operation(summary = "Lista las organizaciones del usuario autenticado",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ListMyOrganizationsBody> listMyOrganizations(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int perPage) {

        return ResponseEntity.ok(organizationService.listMyOrganizations(page, perPage));
    }

    @PostMapping("/v1/orgs")
    @Operation(summary = "Crea una nueva organización",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<OrganizationDTO> createOrganization(
            @Valid @RequestBody CreateOrganizationBody request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(organizationService.createOrganization(request));
    }

    @GetMapping("/v1/orgs/{orgName}")
    @Operation(summary = "Obtiene los datos públicos de una organización")
    public ResponseEntity<OrganizationDTO> getOrganization(
            @PathVariable String orgName) {

        return ResponseEntity.ok(organizationService.getOrganization(orgName));
    }

    @PatchMapping("/v1/orgs/{orgName}")
    @Operation(summary = "Actualiza los datos de la organización",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<OrganizationDTO> updateOrganization(
            @PathVariable String orgName,
            @Valid @RequestBody UpdateOrganizationBody request) {

        return ResponseEntity.ok(organizationService.updateOrganization(orgName, request));
    }

    @DeleteMapping("/v1/orgs/{orgName}")
    @Operation(summary = "Elimina la organización",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteOrganization(
            @PathVariable String orgName) {

        organizationService.deleteOrganization(orgName);
        return ResponseEntity.noContent().build();
    }
}
