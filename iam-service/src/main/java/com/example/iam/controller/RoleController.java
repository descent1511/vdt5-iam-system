package com.example.iam.controller;

import com.example.iam.dto.RoleDTO;
import com.example.iam.dto.ScopeDTO;
import com.example.iam.entity.Role;
import com.example.iam.entity.Scope;
import com.example.iam.mapper.RoleMapper;
import com.example.iam.mapper.ScopeMapper;
import com.example.iam.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final RoleMapper roleMapper;
    private final ScopeMapper scopeMapper;

    @GetMapping
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        List<RoleDTO> dtos = roles.stream()
                .map(roleMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<RoleDTO> createRole(@RequestBody RoleDTO dto) {
        Role created = roleService.createRole(roleMapper.toEntity(dto));
        return ResponseEntity.ok(roleMapper.toDTO(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> getRole(@PathVariable Long id) {
        Role role = roleService.getRole(id);
        return ResponseEntity.ok(roleMapper.toDTO(role));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleDTO> updateRole(@PathVariable Long id, @RequestBody RoleDTO dto) {
        Role updated = roleService.updateRole(id, roleMapper.toEntity(dto));
        return ResponseEntity.ok(roleMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/scopes")
    public ResponseEntity<List<ScopeDTO>> getRoleScopes(@PathVariable Long id) {
        Set<Scope> scopes = roleService.getRoleScopes(id);
        List<ScopeDTO> dtos = scopes.stream()
                .map(scopeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/{id}/scopes")
    public ResponseEntity<RoleDTO> addScopeToRole(
            @PathVariable Long id,
            @RequestBody Long scopeId) {
        Role updated = roleService.addScopeToRole(id, scopeId);
        return ResponseEntity.ok(roleMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}/scopes/{scopeId}")
    public ResponseEntity<RoleDTO> removeScopeFromRole(
            @PathVariable Long id,
            @PathVariable Long scopeId) {
        Role updated = roleService.removeScopeFromRole(id, scopeId);
        return ResponseEntity.ok(roleMapper.toDTO(updated));
    }
}
