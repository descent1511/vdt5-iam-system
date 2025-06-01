package com.example.iam.service;

import com.example.iam.entity.Permission;
import com.example.iam.entity.Role;
import com.example.iam.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Set;
import com.example.iam.dto.RoleDTO;
import com.example.iam.repository.PermissionRepository;
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Transactional
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    public Role getRole(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }

    @Transactional
    public Role updateRole(Long id, RoleDTO roleDTO) {
        Role existingRole = getRole(id);
        System.out.println("Existing role: " + existingRole);
        existingRole.setName(roleDTO.getName());
        existingRole.setDescription(roleDTO.getDescription());
       
        if (roleDTO.getPermissions() != null) {
            Set<Permission> permissions = permissionRepository.findByNameIn(roleDTO.getPermissions())
                    .stream().collect(Collectors.toSet());
            existingRole.setPermissions(permissions);
        }
        
        return roleRepository.save(existingRole);
    }

    @Transactional
    public void deleteRole(Long id) {
        Role role = getRole(id);
        roleRepository.delete(role);
    }

    public Set<Permission> getRolePermissions(Long id) {
        Role role = getRole(id);
        return role.getPermissions();
    }

} 