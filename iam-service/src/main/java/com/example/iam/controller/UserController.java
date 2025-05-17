package com.example.iam.controller;

import com.example.iam.dto.UserDTO;
import com.example.iam.dto.RoleDTO;
import com.example.iam.dto.ScopeDTO;
import com.example.iam.entity.Role;
import com.example.iam.entity.Scope;
import com.example.iam.entity.User;
import com.example.iam.mapper.UserMapper;
import com.example.iam.mapper.RoleMapper;
import com.example.iam.mapper.ScopeMapper;
import com.example.iam.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final ScopeMapper scopeMapper;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(userMapper.toDTOList(users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(userMapper.toDTO(user));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        User updated = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(userMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/roles")
    public ResponseEntity<List<RoleDTO>> getUserRoles(@PathVariable Long id) {
        Set<Role> roles = userService.getUserRoles(id);
        return ResponseEntity.ok(
                roles.stream().map(roleMapper::toDTO).toList()
        );
    }

    @PostMapping("/{id}/roles/{roleId}")
    public ResponseEntity<UserDTO> addRoleToUser(@PathVariable Long id, @PathVariable Long roleId) {
        User user = userService.addRoleToUser(id, roleId);
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    @DeleteMapping("/{id}/roles/{roleId}")
    public ResponseEntity<UserDTO> removeRoleFromUser(@PathVariable Long id, @PathVariable Long roleId) {
        User user = userService.removeRoleFromUser(id, roleId);
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    @GetMapping("/{id}/scopes")
    @PreAuthorize("hasAuthority('read:user')")
    public ResponseEntity<List<ScopeDTO>> getUserScopes(@PathVariable Long id) {
        Set<Scope> scopes = userService.getUserScopes(id);
        return ResponseEntity.ok(
                scopes.stream().map(scopeMapper::toDTO).toList()
        );
    }

    @PostMapping("/{id}/scopes/{scopeId}")
    public ResponseEntity<UserDTO> addScopeToUser(@PathVariable Long id, @PathVariable Long scopeId) {
        User user = userService.addScopeToUser(id, scopeId);
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    @DeleteMapping("/{id}/scopes/{scopeId}")
    public ResponseEntity<UserDTO> removeScopeFromUser(@PathVariable Long id, @PathVariable Long scopeId) {
        User user = userService.removeScopeFromUser(id, scopeId);
        return ResponseEntity.ok(userMapper.toDTO(user));
    }
}
