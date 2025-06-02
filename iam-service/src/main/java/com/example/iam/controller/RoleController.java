package com.example.iam.controller;

import com.example.iam.dto.RoleDTO;
import com.example.iam.entity.Role;
import com.example.iam.mapper.RoleMapper;
import com.example.iam.security.annotation.RequirePermission;
import com.example.iam.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final RoleMapper roleMapper;

    @GetMapping
    @RequirePermission(value = "ROLE_READ", description = "View list of roles")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        List<RoleDTO> dtos = roles.stream()
                .map(roleMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    @RequirePermission(value = "ROLE_CREATE", description = "Create new role")
    public ResponseEntity<RoleDTO> createRole(@RequestBody RoleDTO dto) {
        Role created = roleService.createRole(dto);
        return ResponseEntity.ok(roleMapper.toDTO(created));
    }

    @GetMapping("/{id}")
    @RequirePermission(value = "ROLE_READ", description = "View role details")
    public ResponseEntity<RoleDTO> getRole(@PathVariable Long id) {
        Role role = roleService.getRole(id);
        return ResponseEntity.ok(roleMapper.toDTO(role));
    }

    @PutMapping("/{id}")
    @RequirePermission(value = "ROLE_UPDATE", description = "Update role information")
    public ResponseEntity<RoleDTO> updateRole(@PathVariable Long id, @RequestBody RoleDTO dto) {
        Role updated = roleService.updateRole(id, dto);
        return ResponseEntity.ok(roleMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(value = "ROLE_DELETE", description = "Delete role")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

}
