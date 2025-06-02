package com.example.iam.controller;

import com.example.iam.dto.OrganizationDTO;
import com.example.iam.service.OrganizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationControllerTest {

    @Mock
    private OrganizationService organizationService;

    @InjectMocks
    private OrganizationController organizationController;

    private OrganizationDTO organizationDTO;

    @BeforeEach
    void setUp() {
        organizationDTO = new OrganizationDTO(1L, "Test Organization", "Test Organization Description", null);
    }

    @Test
    void getAllOrganizations_ShouldReturnListOfOrganizations() {
        List<OrganizationDTO> organizations = Arrays.asList(organizationDTO);
        when(organizationService.getAllOrganizations()).thenReturn(organizations);

        ResponseEntity<List<OrganizationDTO>> response = organizationController.getAllOrganizations();

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(organizations, response.getBody());
        verify(organizationService).getAllOrganizations();
    }

    @Test
    void createOrganization_ShouldReturnCreatedOrganization() {
        when(organizationService.createOrganization(any(OrganizationDTO.class))).thenReturn(organizationDTO);

        ResponseEntity<OrganizationDTO> response = organizationController.createOrganization(organizationDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(organizationDTO, response.getBody());
        verify(organizationService).createOrganization(any(OrganizationDTO.class));
    }

    @Test
    void getOrganization_ShouldReturnOrganizationById() {
        when(organizationService.getOrganization(1L)).thenReturn(organizationDTO);

        ResponseEntity<OrganizationDTO> response = organizationController.getOrganization(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(organizationDTO, response.getBody());
        verify(organizationService).getOrganization(1L);
    }

    @Test
    void updateOrganization_ShouldReturnUpdatedOrganization() {
        when(organizationService.updateOrganization(eq(1L), any(OrganizationDTO.class))).thenReturn(organizationDTO);

        ResponseEntity<OrganizationDTO> response = organizationController.updateOrganization(1L, organizationDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(organizationDTO, response.getBody());
        verify(organizationService).updateOrganization(eq(1L), any(OrganizationDTO.class));
    }

    @Test
    void deleteOrganization_ShouldReturnNoContent() {
        ResponseEntity<Void> response = organizationController.deleteOrganization(1L);

        assertNotNull(response);
        assertEquals(204, response.getStatusCode().value());
        verify(organizationService).deleteOrganization(1L);
    }
} 