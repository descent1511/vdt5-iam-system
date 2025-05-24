package com.example.iam.controller;

import com.example.iam.entity.Organization;
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

    private Organization organization;

    @BeforeEach
    void setUp() {
        organization = new Organization();
        organization.setId(1L);
        organization.setName("Test Organization");
        organization.setDescription("Test Organization Description");
    }

    @Test
    void getAllOrganizations_ShouldReturnListOfOrganizations() {
        // Arrange
        List<Organization> organizations = Arrays.asList(organization);
        when(organizationService.getAllOrganizations()).thenReturn(organizations);

        // Act
        ResponseEntity<List<Organization>> response = organizationController.getAllOrganizations();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(organizations, response.getBody());
        verify(organizationService).getAllOrganizations();
    }

    @Test
    void createOrganization_ShouldReturnCreatedOrganization() {
        // Arrange
        when(organizationService.createOrganization(any(Organization.class))).thenReturn(organization);

        // Act
        ResponseEntity<Organization> response = organizationController.createOrganization(organization);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(organization, response.getBody());
        verify(organizationService).createOrganization(any(Organization.class));
    }

    @Test
    void getOrganization_ShouldReturnOrganizationById() {
        // Arrange
        when(organizationService.getOrganization(1L)).thenReturn(organization);

        // Act
        ResponseEntity<Organization> response = organizationController.getOrganization(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(organization, response.getBody());
        verify(organizationService).getOrganization(1L);
    }

    @Test
    void updateOrganization_ShouldReturnUpdatedOrganization() {
        // Arrange
        when(organizationService.updateOrganization(eq(1L), any(Organization.class))).thenReturn(organization);

        // Act
        ResponseEntity<Organization> response = organizationController.updateOrganization(1L, organization);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(organization, response.getBody());
        verify(organizationService).updateOrganization(eq(1L), any(Organization.class));
    }

    @Test
    void deleteOrganization_ShouldReturnNoContent() {
        // Act
        ResponseEntity<Void> response = organizationController.deleteOrganization(1L);

        // Assert
        assertNotNull(response);
        assertEquals(204, response.getStatusCode().value());
        verify(organizationService).deleteOrganization(1L);
    }
} 