package com.example.iam.service;

import com.example.iam.entity.Scope;
import com.example.iam.dto.ScopeDTO;
import com.example.iam.entity.Permission;
import com.example.iam.repository.ScopeRepository;
import com.example.iam.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashSet;
import com.example.iam.entity.Organization;
import com.example.iam.repository.OrganizationRepository;

@Service
@RequiredArgsConstructor
public class ScopeService {

    @Autowired
    private ScopeRepository scopeRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    public List<Scope> getAllScopes() {
        return scopeRepository.findAll();
    }


    @Transactional
    public Scope createScope(ScopeDTO dto) {
        Scope scope = new Scope();
        scope.setName(dto.getName());
        scope.setDescription(dto.getDescription());
        
        if (dto.getPermissions() != null && !dto.getPermissions().isEmpty()) {
            Set<Permission> permissions = permissionRepository.findByNameIn(dto.getPermissions());
            scope.setPermissions(permissions);
        }
        
        return scopeRepository.save(scope);
    }

    public Scope getScope(Long id) {
        return scopeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scope not found"));
    }

    @Transactional
    public Scope updateScope(Long id, ScopeDTO scopeDTO) {
        Scope existingScope = getScope(id);
        if (scopeDTO.getName() != null && !scopeDTO.getName().trim().isEmpty()) {
            existingScope.setName(scopeDTO.getName());
        }
        if (scopeDTO.getDescription() != null) {
            existingScope.setDescription(scopeDTO.getDescription());
        }
        
        if (scopeDTO.getPermissions() != null) {
            Set<Permission> permissions = permissionRepository.findByNameIn(new HashSet<>(scopeDTO.getPermissions()));
            existingScope.getPermissions().clear();
            for (Permission permission : permissions) {
                existingScope.addPermission(permission);
            }
        }
        
        return scopeRepository.save(existingScope);
    }

    @Transactional
    public void deleteScope(Long id) {
        Scope scope = getScope(id);
        scopeRepository.delete(scope);
    }
} 