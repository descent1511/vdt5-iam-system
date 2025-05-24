package com.example.iam.controller;

import com.example.iam.dto.ResourceDTO;
import com.example.iam.entity.Resource;
import com.example.iam.mapper.ResourceMapper;
import com.example.iam.service.ResourceService;
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
class ResourceControllerTest {

    @Mock
    private ResourceService resourceService;

    @Mock
    private ResourceMapper resourceMapper;

    @InjectMocks
    private ResourceController resourceController;

    private Resource resource;
    private ResourceDTO resourceDTO;

    @BeforeEach
    void setUp() {
        resource = new Resource();
        resource.setId(1L);
        resource.setName("USER_RESOURCE");
        resource.setDescription("User management resource");

        resourceDTO = new ResourceDTO();
        resourceDTO.setId(1L);
        resourceDTO.setName("USER_RESOURCE");
        resourceDTO.setDescription("User management resource");
    }

    @Test
    void getAll_ShouldReturnListOfResources() {
        // Arrange
        List<Resource> resources = Arrays.asList(resource);
        List<ResourceDTO> resourceDTOs = Arrays.asList(resourceDTO);
        when(resourceService.getAll()).thenReturn(resources);
        when(resourceMapper.toDTOList(anyList())).thenReturn(resourceDTOs);

        // Act
        ResponseEntity<List<ResourceDTO>> response = resourceController.getAll();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(resourceDTOs, response.getBody());
        verify(resourceService).getAll();
    }

    @Test
    void getById_ShouldReturnResourceById() {
        // Arrange
        when(resourceService.getById(1L)).thenReturn(resource);
        when(resourceMapper.toDTO(any(Resource.class))).thenReturn(resourceDTO);

        // Act
        ResponseEntity<ResourceDTO> response = resourceController.getById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(resourceDTO, response.getBody());
        verify(resourceService).getById(1L);
    }

    @Test
    void create_ShouldReturnCreatedResource() {
        // Arrange
        when(resourceMapper.toEntity(any(ResourceDTO.class))).thenReturn(resource);
        when(resourceService.create(any(Resource.class))).thenReturn(resource);
        when(resourceMapper.toDTO(any(Resource.class))).thenReturn(resourceDTO);

        // Act
        ResponseEntity<ResourceDTO> response = resourceController.create(resourceDTO);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(resourceDTO, response.getBody());
        verify(resourceService).create(any(Resource.class));
    }

    @Test
    void update_ShouldReturnUpdatedResource() {
        // Arrange
        when(resourceMapper.toEntity(any(ResourceDTO.class))).thenReturn(resource);
        when(resourceService.update(eq(1L), any(Resource.class))).thenReturn(resource);
        when(resourceMapper.toDTO(any(Resource.class))).thenReturn(resourceDTO);

        // Act
        ResponseEntity<ResourceDTO> response = resourceController.update(1L, resourceDTO);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(resourceDTO, response.getBody());
        verify(resourceService).update(eq(1L), any(Resource.class));
    }

    @Test
    void delete_ShouldReturnNoContent() {
        // Act
        ResponseEntity<Void> response = resourceController.delete(1L);

        // Assert
        assertNotNull(response);
        assertEquals(204, response.getStatusCode().value());
        verify(resourceService).delete(1L);
    }
} 