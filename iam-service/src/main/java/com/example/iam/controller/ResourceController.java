package com.example.iam.controller;

import com.example.iam.dto.ResourceDTO;
import com.example.iam.entity.Resource;
import com.example.iam.mapper.ResourceMapper;
import com.example.iam.service.ResourceService;
import com.example.iam.service.ResourceDiscoveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.iam.security.annotation.RequirePermission;
import java.util.List;

@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;
    private final ResourceMapper resourceMapper;
    private final ResourceDiscoveryService resourceDiscoveryService;

    @GetMapping
    @RequirePermission(value = "RESOURCE_READ", description = "View list of resources")
    public ResponseEntity<List<ResourceDTO>> getAll() {
        List<Resource> list = resourceService.getAll();
        return ResponseEntity.ok(resourceMapper.toDTOList(list));
    }

    @GetMapping("/{id}")
    @RequirePermission(value = "RESOURCE_READ", description = "View resource details")
    public ResponseEntity<ResourceDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(resourceMapper.toDTO(resourceService.getById(id)));
    }

    @PostMapping
    @RequirePermission(value = "RESOURCE_CREATE", description = "Create new resource")
    public ResponseEntity<ResourceDTO> create(@RequestBody ResourceDTO dto) {
        Resource resource = resourceMapper.toEntity(dto);
        return ResponseEntity.ok(resourceMapper.toDTO(resourceService.create(resource)));
    }

    @PutMapping("/{id}")
    @RequirePermission(value = "RESOURCE_UPDATE", description = "Update resource information")
    public ResponseEntity<ResourceDTO> update(@PathVariable Long id, @RequestBody ResourceDTO dto) {
        Resource resource = resourceMapper.toEntity(dto);
        return ResponseEntity.ok(resourceMapper.toDTO(resourceService.update(id, resource)));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(value = "RESOURCE_DELETE", description = "Delete resource")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        resourceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/discover")
    public ResponseEntity<String> discoverResources() {
        resourceDiscoveryService.discoverAndRegisterResources();
        return ResponseEntity.ok("Resource discovery completed successfully");
    }
}
