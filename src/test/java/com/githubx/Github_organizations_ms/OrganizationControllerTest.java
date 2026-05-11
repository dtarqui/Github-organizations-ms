package com.githubx.Github_organizations_ms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.githubx.Github_organizations_ms.config.security.SecurityConfig;
import com.githubx.Github_organizations_ms.generated.model.CreateOrganizationBody;
import com.githubx.Github_organizations_ms.generated.model.OrganizationDTO;
import com.githubx.Github_organizations_ms.generated.model.OrgVisibility;
import com.githubx.Github_organizations_ms.service.contratos.OrganizationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrganizationController.class)
@Import(SecurityConfig.class)
class OrganizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrganizationService organizationService;

    private OrganizationDTO buildSampleResponse() {
        return new OrganizationDTO()
                .id("00000000-0000-4000-8000-000000000001")
                .name("acme-org")
                .displayName("Acme Organization")
                .description("Una organización de prueba")
                .website("https://acme.com")
                .visibility(OrgVisibility.PUBLIC)
                .membersCount(1)
                .reposCount(0)
                .teamsCount(0)
                .createdAt("2024-01-01T00:00:00Z")
                .updatedAt("2024-01-01T00:00:00Z");
    }

    @Test
    @WithMockUser
    void debeCrearOrganizacion() throws Exception {
        OrganizationDTO response = buildSampleResponse();
        Mockito.when(organizationService.createOrganization(any())).thenReturn(response);

        CreateOrganizationBody request = new CreateOrganizationBody()
                .name("acme-org")
                .displayName("Acme Organization")
                .description("Descripción")
                .visibility(OrgVisibility.PUBLIC);

        mockMvc.perform(post("/v1/orgs")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("acme-org"))
                .andExpect(jsonPath("$.displayName").value("Acme Organization"));
    }

    @Test
    @WithAnonymousUser
    void debeObtenerOrganizacionSinToken() throws Exception {
        OrganizationDTO response = buildSampleResponse();
        Mockito.when(organizationService.getOrganization("acme-org")).thenReturn(response);

        mockMvc.perform(get("/v1/orgs/acme-org"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("acme-org"));
    }

    @Test
    @WithMockUser
    void debeRetornar400SiNombreEsInvalido() throws Exception {
        CreateOrganizationBody requestInvalido = new CreateOrganizationBody()
                .name("ab") // muy corto, mínimo 3
                .displayName("Display")
                .visibility(OrgVisibility.PUBLIC);

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
