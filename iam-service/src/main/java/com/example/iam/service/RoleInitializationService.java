package com.example.iam.service;

import com.example.iam.entity.Permission;
import com.example.iam.entity.Role;
import com.example.iam.entity.User;
import com.example.iam.repository.PermissionRepository;
import com.example.iam.repository.RoleRepository;
import com.example.iam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleInitializationService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    @Transactional
    public void initializeRoles() {
        // Get all existing permissions from database
        List<Permission> allPermissions = permissionRepository.findAll();
        
        // Create admin role with all permissions if not exists
        Optional<Role> existingAdminRole = roleRepository.findByName("ROLE_ADMIN");
        if (existingAdminRole.isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            adminRole.setDescription("Administrator role with all permissions");
            adminRole.setPermissions(new HashSet<>(allPermissions));
            roleRepository.save(adminRole);
            log.info("Created new role: ROLE_ADMIN");
        }
        
        // Create manager role with limited permissions if not exists
        Optional<Role> existingManagerRole = roleRepository.findByName("ROLE_MANAGER");
        if (existingManagerRole.isEmpty()) {
            Set<Permission> managerPermissions = new HashSet<>();
            for (Permission permission : allPermissions) {
                if (!permission.getName().contains("DELETE") && !permission.getName().contains("UPDATE")) {
                    managerPermissions.add(permission);
                }
            }
            
            Role managerRole = new Role();
            managerRole.setName("ROLE_MANAGER");
            managerRole.setDescription("Manager role with view and update permissions");
            managerRole.setPermissions(managerPermissions);
            roleRepository.save(managerRole);
            log.info("Created new role: ROLE_MANAGER");
        }
        
        // Create user role with basic permissions if not exists
        Optional<Role> existingUserRole = roleRepository.findByName("ROLE_USER");
        if (existingUserRole.isEmpty()) {
            Set<Permission> userPermissions = new HashSet<>();
            for (Permission permission : allPermissions) {
                if (permission.getName().equals("VIEW_OWN_PROFILE") || permission.getName().equals("UPDATE_OWN_PROFILE")) {
                    userPermissions.add(permission);
                }
            }
            
            Role userRole = new Role();
            userRole.setName("ROLE_USER");
            userRole.setDescription("Regular user role with basic permissions");
            userRole.setPermissions(userPermissions);
            roleRepository.save(userRole);
            log.info("Created new role: ROLE_USER");
        }

        // Create default users if they don't exist
        createUserIfNotExists("admin", "admin123", "admin@example.com", "Admin User", roleRepository.findByName("ROLE_ADMIN").orElseThrow());
        createUserIfNotExists("manager", "manager123", "manager@example.com", "Manager User", roleRepository.findByName("ROLE_MANAGER").orElseThrow());
        createUserIfNotExists("user", "user123", "user@example.com", "Regular User", roleRepository.findByName("ROLE_USER").orElseThrow());
    }

    private void createUserIfNotExists(String username, String password, String email, String fullName, Role role) {
        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isEmpty()) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setEmail(email);
            user.setFullName(fullName);
            user.setEnabled(true);
            user.setRoles(new HashSet<>(List.of(role)));
            userRepository.save(user);
            log.info("Created {} user with username: {}", role.getName(), username);
        }
    }
}