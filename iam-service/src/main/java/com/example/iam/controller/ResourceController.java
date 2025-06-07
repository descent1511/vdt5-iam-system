package com.example.iam.controller;

import com.example.iam.dto.ResourceDTO;
import com.example.iam.entity.Resource;
import com.example.iam.mapper.ResourceMapper;
import com.example.iam.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import com.example.iam.service.ServiceRegistryService;
@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;
    private final ResourceMapper resourceMapper;
    private final ServiceRegistryService serviceRegistryService;
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<ResourceDTO>> getAll() {
        List<Resource> list = resourceService.getAll();
        return ResponseEntity.ok(resourceMapper.toDTOList(list));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ResourceDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(resourceMapper.toDTO(resourceService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ResourceDTO> create(@RequestBody ResourceDTO dto) {
        return ResponseEntity.ok(resourceMapper.toDTO(resourceService.create(dto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ResourceDTO> update(@PathVariable Long id, @RequestBody ResourceDTO dto) {
        return ResponseEntity.ok(resourceMapper.toDTO(resourceService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        resourceService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/discover")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<Void> discovery() {
        serviceRegistryService.reloadAllResources();
        return ResponseEntity.ok().build();
    }
}
