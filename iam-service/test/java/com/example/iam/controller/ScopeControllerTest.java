package com.example.iam.controller;

import com.example.iam.dto.ScopeDTO;
import com.example.iam.entity.Scope;
import com.example.iam.mapper.ScopeMapper;
import com.example.iam.service.ScopeService;
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
class ScopeControllerTest {

    @Mock
    private ScopeService scopeService;

    @Mock
    private ScopeMapper scopeMapper;

    @InjectMocks
    private ScopeController scopeController;

    private Scope scope;
    private ScopeDTO scopeDTO;

    @BeforeEach
    void setUp() {
        scope = new Scope();
        scope.setId(1L);
        scope.setName("SCOPE_ADMIN");
        scope.setDescription("Administrator scope");

        scopeDTO = new ScopeDTO();
        scopeDTO.setId(1L);
        scopeDTO.setName("SCOPE_ADMIN");
        scopeDTO.setDescription("Administrator scope");
    }

    @Test
    void getAllScopes_ShouldReturnListOfScopes() {
        // Arrange
        List<Scope> scopes = Arrays.asList(scope);
        List<ScopeDTO> scopeDTOs = Arrays.asList(scopeDTO);
        when(scopeService.getAllScopes()).thenReturn(scopes);
        when(scopeMapper.toDTO(any(Scope.class))).thenReturn(scopeDTO);

        // Act
        ResponseEntity<List<ScopeDTO>> response = scopeController.getAllScopes();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(scopeDTOs, response.getBody());
        verify(scopeService).getAllScopes();
    }

    @Test
    void createScope_ShouldReturnCreatedScope() {
        // Arrange
        when(scopeService.createScope(any(ScopeDTO.class))).thenReturn(scope);
        when(scopeMapper.toDTO(any(Scope.class))).thenReturn(scopeDTO);

        // Act
        ResponseEntity<ScopeDTO> response = scopeController.createScope(scopeDTO);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(scopeDTO, response.getBody());
        verify(scopeService).createScope(any(ScopeDTO.class));
    }

    @Test
    void getScope_ShouldReturnScopeById() {
        // Arrange
        when(scopeService.getScope(1L)).thenReturn(scope);
        when(scopeMapper.toDTO(any(Scope.class))).thenReturn(scopeDTO);

        // Act
        ResponseEntity<ScopeDTO> response = scopeController.getScope(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(scopeDTO, response.getBody());
        verify(scopeService).getScope(1L);
    }

    @Test
    void updateScope_ShouldReturnUpdatedScope() {
        // Arrange
        when(scopeService.updateScope(eq(1L), any(ScopeDTO.class))).thenReturn(scope);
        when(scopeMapper.toDTO(any(Scope.class))).thenReturn(scopeDTO);

        // Act
        ResponseEntity<ScopeDTO> response = scopeController.updateScope(1L, scopeDTO);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(scopeDTO, response.getBody());
        verify(scopeService).updateScope(eq(1L), any(ScopeDTO.class));
    }

    @Test
    void deleteScope_ShouldReturnNoContent() {
        // Act
        ResponseEntity<Void> response = scopeController.deleteScope(1L);

        // Assert
        assertNotNull(response);
        assertEquals(204, response.getStatusCode().value());
        verify(scopeService).deleteScope(1L);
    }
} 