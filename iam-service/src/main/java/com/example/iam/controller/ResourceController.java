package com.example.iam.controller;

import com.example.iam.dto.ResourceDTO;
import com.example.iam.entity.Resource;
import com.example.iam.mapper.ResourceMapper;
import com.example.iam.service.ResourceService;
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

    @GetMapping
    @RequirePermission
    public ResponseEntity<List<ResourceDTO>> getAll() {
        List<Resource> list = resourceService.getAll();
        return ResponseEntity.ok(resourceMapper.toDTOList(list));
    }

    @GetMapping("/{id}")
    @RequirePermission
    public ResponseEntity<ResourceDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(resourceMapper.toDTO(resourceService.getById(id)));
    }

    @PostMapping
    @RequirePermission
    public ResponseEntity<ResourceDTO> create(@RequestBody ResourceDTO dto) {
        Resource resource = resourceMapper.toEntity(dto);
        return ResponseEntity.ok(resourceMapper.toDTO(resourceService.create(resource)));
    }

    @PutMapping("/{id}")
    @RequirePermission
    public ResponseEntity<ResourceDTO> update(@PathVariable Long id, @RequestBody ResourceDTO dto) {
        Resource resource = resourceMapper.toEntity(dto);
        return ResponseEntity.ok(resourceMapper.toDTO(resourceService.update(id, resource)));
    }

    @DeleteMapping("/{id}")
    @RequirePermission
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        resourceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
