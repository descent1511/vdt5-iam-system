package com.example.iam.service;

import com.example.iam.entity.Resource;
import com.example.iam.repository.ResourceRepository;
import com.example.iam.repository.PermissionRepository;
import com.example.iam.mapper.ResourceMapper;
import com.example.iam.dto.ResourceDTO;
import com.example.iam.entity.Permission;
// import com.example.iam.config.annotation.RequiresOrganizationContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final PermissionRepository permissionRepository;
    private final ResourceMapper resourceMapper;

    public List<Resource> getAll() {
        return resourceRepository.findAll();
    }

    public Resource getById(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found"));
    }

    @Transactional
    public Resource create(ResourceDTO dto) {
        Resource resource = resourceMapper.toEntity(dto);
        if (resourceRepository.existsByName(resource.getName())) {
            throw new RuntimeException("Resource name already exists");
        }

        if (dto.getPermissions() != null && !dto.getPermissions().isEmpty()) {
            Set<Permission> permissions = getPermissionsFromNames(dto.getPermissions());
            resource.setPermissions(permissions);
        }

        return resourceRepository.save(resource);
    }

    @Transactional
    public Resource update(Long id, ResourceDTO dto) {
        Resource existing = getById(id);
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setMethod(dto.getMethod());
        existing.setPath(dto.getPath());

        if (dto.getPermissions() != null) {
            Set<Permission> permissions = getPermissionsFromNames(dto.getPermissions());
            existing.setPermissions(permissions);
        }

        return resourceRepository.save(existing);
    }

    private Set<Permission> getPermissionsFromNames(Set<String> permissionNames) {
        return permissionNames.stream()
                .map(name -> permissionRepository.findByName(name)
                        .orElseGet(() -> {
                            Permission newPermission = new Permission(name);
                            return permissionRepository.save(newPermission);
                        }))
                .collect(Collectors.toSet());
    }

    @Transactional
    public void delete(Long id) {
        resourceRepository.deleteById(id);
    }
}
