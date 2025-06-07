package com.example.iam.controller;

import com.example.iam.dto.PolicyDTO;
import com.example.iam.entity.Policy;
import com.example.iam.service.PolicyService;
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
class PolicyControllerTest {

    @Mock
    private PolicyService policyService;

    @InjectMocks
    private PolicyController policyController;

    private PolicyDTO policyDTO;

    @BeforeEach
    void setUp() {
        policyDTO = new PolicyDTO();
        policyDTO.setId(1L);
        policyDTO.setSubjectId(1L);
        policyDTO.setSubjectType(Policy.SubjectType.USER);
        policyDTO.setResourceId(1L);
        policyDTO.setScopeId(1L);
        policyDTO.setConditionJson("{\"condition\": \"value\"}");
        policyDTO.setDescription("Test Policy Description");
    }

    @Test
    void getAll_ShouldReturnListOfPolicies() {
        // Arrange
        List<PolicyDTO> policies = Arrays.asList(policyDTO);
        when(policyService.getAllPolicies()).thenReturn(policies);

        // Act
        ResponseEntity<List<PolicyDTO>> response = policyController.getAll();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(policies, response.getBody());
        verify(policyService).getAllPolicies();
    }

    @Test
    void get_ShouldReturnPolicyById() {
        // Arrange
        when(policyService.getPolicyById(1L)).thenReturn(policyDTO);

        // Act
        ResponseEntity<PolicyDTO> response = policyController.get(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(policyDTO, response.getBody());
        verify(policyService).getPolicyById(1L);
    }

    @Test
    void create_ShouldReturnCreatedPolicy() {
        // Arrange
        when(policyService.createPolicy(any(PolicyDTO.class))).thenReturn(policyDTO);

        // Act
        ResponseEntity<PolicyDTO> response = policyController.create(policyDTO);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(policyDTO, response.getBody());
        verify(policyService).createPolicy(any(PolicyDTO.class));
    }

    @Test
    void update_ShouldReturnUpdatedPolicy() {
        // Arrange
        when(policyService.updatePolicy(eq(1L), any(PolicyDTO.class))).thenReturn(policyDTO);

        // Act
        ResponseEntity<PolicyDTO> response = policyController.update(1L, policyDTO);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(policyDTO, response.getBody());
        verify(policyService).updatePolicy(eq(1L), any(PolicyDTO.class));
    }

    @Test
    void delete_ShouldReturnNoContent() {
        // Act
        ResponseEntity<Void> response = policyController.delete(1L);

        // Assert
        assertNotNull(response);
        assertEquals(204, response.getStatusCode().value());
        verify(policyService).deletePolicy(1L);
    }
} 