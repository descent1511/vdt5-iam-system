package com.example.iam.controller;

import com.example.iam.entity.Role;
import com.example.iam.entity.Scope;
import com.example.iam.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        return ResponseEntity.ok(roleService.createRole(role));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> getRole(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getRole(id));
    }

    @PutMapping("/{id}")

    public ResponseEntity<Role> updateRole(@PathVariable Long id, @RequestBody Role role) {
        return ResponseEntity.ok(roleService.updateRole(id, role));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/scopes")
    public ResponseEntity<Set<Scope>> getRoleScopes(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getRoleScopes(id));
    }

    @PostMapping("/{id}/scopes")
    public ResponseEntity<Role> addScopeToRole(
            @PathVariable Long id,
            @RequestBody Long scopeId) {
        return ResponseEntity.ok(roleService.addScopeToRole(id, scopeId));
    }

    @DeleteMapping("/{id}/scopes/{scopeId}")
    public ResponseEntity<Role> removeScopeFromRole(
            @PathVariable Long id,
            @PathVariable Long scopeId) {
        return ResponseEntity.ok(roleService.removeScopeFromRole(id, scopeId));
    }


} 