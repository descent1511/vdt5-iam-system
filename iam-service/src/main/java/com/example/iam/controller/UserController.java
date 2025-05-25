package com.example.iam.controller;

import com.example.iam.dto.UserDTO;
import com.example.iam.entity.User;
import com.example.iam.mapper.UserMapper;
import com.example.iam.service.UserService;
import com.example.iam.security.annotation.RequirePermission;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        return ResponseEntity.ok(userMapper.toDTO(currentUser));
    }

    @GetMapping
    @RequirePermission(value = "USER_READ", description = "View list of users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(userMapper.toDTOList(users));
    }

    @GetMapping("/{id}")
    @RequirePermission(value = "USER_READ", description = "View user details")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    @PutMapping("/{id}")
    @RequirePermission(value = "USER_UPDATE", description = "Update user information")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        User updated = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(userMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(value = "USER_DELETE", description = "Delete user")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
}
