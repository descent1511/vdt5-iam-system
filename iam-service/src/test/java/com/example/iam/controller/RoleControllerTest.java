package com.example.iam.controller;

import com.example.iam.dto.RoleDTO;
import com.example.iam.entity.Role;
import com.example.iam.mapper.RoleMapper;
import com.example.iam.service.RoleService;
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
class RoleControllerTest {

    @Mock
    private RoleService roleService;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleController roleController;

    private Role role;
    private RoleDTO roleDTO;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");
        role.setDescription("Administrator role");

        roleDTO = new RoleDTO();
        roleDTO.setId(1L);
        roleDTO.setName("ROLE_ADMIN");
        roleDTO.setDescription("Administrator role");
    }

    @Test
    void getAllRoles_ShouldReturnListOfRoles() {
        // Arrange
        List<Role> roles = Arrays.asList(role);
        List<RoleDTO> roleDTOs = Arrays.asList(roleDTO);
        when(roleService.getAllRoles()).thenReturn(roles);
        when(roleMapper.toDTO(any(Role.class))).thenReturn(roleDTO);

        // Act
        ResponseEntity<List<RoleDTO>> response = roleController.getAllRoles();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(roleDTOs, response.getBody());
        verify(roleService).getAllRoles();
    }

    @Test
    void createRole_ShouldReturnCreatedRole() {
        // Arrange
        when(roleMapper.toEntity(any(RoleDTO.class))).thenReturn(role);
        when(roleService.createRole(any(Role.class))).thenReturn(role);
        when(roleMapper.toDTO(any(Role.class))).thenReturn(roleDTO);

        // Act
        ResponseEntity<RoleDTO> response = roleController.createRole(roleDTO);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(roleDTO, response.getBody());
        verify(roleService).createRole(any(Role.class));
    }

    @Test
    void getRole_ShouldReturnRoleById() {
        // Arrange
        when(roleService.getRole(1L)).thenReturn(role);
        when(roleMapper.toDTO(any(Role.class))).thenReturn(roleDTO);

        // Act
        ResponseEntity<RoleDTO> response = roleController.getRole(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(roleDTO, response.getBody());
        verify(roleService).getRole(1L);
    }

    @Test
    void updateRole_ShouldReturnUpdatedRole() {
        // Arrange
        when(roleMapper.toEntity(any(RoleDTO.class))).thenReturn(role);
        when(roleService.updateRole(eq(1L), any(Role.class))).thenReturn(role);
        when(roleMapper.toDTO(any(Role.class))).thenReturn(roleDTO);

        // Act
        ResponseEntity<RoleDTO> response = roleController.updateRole(1L, roleDTO);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(roleDTO, response.getBody());
        verify(roleService).updateRole(eq(1L), any(Role.class));
    }

    @Test
    void deleteRole_ShouldReturnNoContent() {
        // Act
        ResponseEntity<Void> response = roleController.deleteRole(1L);

        // Assert
        assertNotNull(response);
        assertEquals(204, response.getStatusCode().value());
        verify(roleService).deleteRole(1L);
    }
} 