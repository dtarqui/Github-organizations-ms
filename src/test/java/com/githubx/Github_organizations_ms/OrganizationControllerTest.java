package com.githubx.Github_organizations_ms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.githubx.Github_organizations_ms.dto.request.CreateOrganizationRequest;
import com.githubx.Github_organizations_ms.dto.response.OrganizationResponse;
import com.githubx.Github_organizations_ms.service.contratos.OrganizationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrganizationController.class)
class OrganizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrganizationService organizationService;

    private OrganizationResponse buildSampleResponse() {
        return new OrganizationResponse(
                UUID.randomUUID(), "acme-org", "Acme Organization",
                "Una organización de prueba", null, "https://acme.com",
                "public", 1, 0, 0,
                "2024-01-01T00:00:00Z", "2024-01-01T00:00:00Z"
        );
    }

    @Test
    @WithMockUser
    void debeCrearOrganizacion() throws Exception {
        OrganizationResponse response = buildSampleResponse();
        Mockito.when(organizationService.createOrganization(any())).thenReturn(response);

        CreateOrganizationRequest request = new CreateOrganizationRequest(
                "acme-org", "Acme Organization", "Descripción", null, "public"
        );

        mockMvc.perform(post("/v1/orgs")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("acme-org"))
                .andExpect(jsonPath("$.displayName").value("Acme Organization"));
    }

    @Test
    void debeObtenerOrganizacionSinToken() throws Exception {
        OrganizationResponse response = buildSampleResponse();
        Mockito.when(organizationService.getOrganization("acme-org")).thenReturn(response);

        mockMvc.perform(get("/v1/orgs/acme-org"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("acme-org"));
    }

    @Test
    @WithMockUser
    void debeRetornar400SiNombreEsInvalido() throws Exception {
        CreateOrganizationRequest requestInvalido = new CreateOrganizationRequest(
                "ab", // muy corto, mínimo 3
                "Display",
                null, null, "public"
        );

        mockMvc.perform(post("/v1/orgs")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void debeEliminarOrganizacion() throws Exception {
        Mockito.doNothing().when(organizationService).deleteOrganization("acme-org");

        mockMvc.perform(delete("/v1/orgs/acme-org").with(csrf()))
                .andExpect(status().isNoContent());
    }
}