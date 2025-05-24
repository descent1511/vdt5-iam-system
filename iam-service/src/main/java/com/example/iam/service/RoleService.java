package com.example.iam.service;

import com.example.iam.entity.Permission;
import com.example.iam.entity.Role;
import com.example.iam.entity.Scope;
import com.example.iam.repository.RoleRepository;
import com.example.iam.repository.ScopeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final ScopeRepository scopeRepository;

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
    public Role updateRole(Long id, Role role) {
        Role existingRole = getRole(id);
        existingRole.setName(role.getName());
        existingRole.setDescription(role.getDescription());
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