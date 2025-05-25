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

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminInitializationService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    @Transactional
    public void initializeAdmin() {
        // Create admin role if not exists
        Optional<Role> existingAdminRole = roleRepository.findByName("ROLE_ADMIN");
        Role adminRole = existingAdminRole.orElseGet(() -> {
            Role role = Role.builder()
                .name("ROLE_ADMIN")
                .description("Administrator role with all permissions")
                .build();
            return roleRepository.save(role);
        });

        // Get all permissions and assign to admin role
        List<Permission> allPermissions = permissionRepository.findAll();
        adminRole.setPermissions(new HashSet<>(allPermissions));
        roleRepository.save(adminRole);
        log.info("Updated admin role with {} permissions", allPermissions.size());

        // Create admin user if not exists
        Optional<User> existingAdminUser = userRepository.findByUsername("admin");
        if (existingAdminUser.isEmpty()) {
            User adminUser = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123")) // Default password
                .email("admin@example.com")
                .fullName("Admin User")
                .enabled(true)
                .roles(new HashSet<>(List.of(adminRole)))
                .build();
            userRepository.save(adminUser);
            log.info("Created admin user with username: admin");
        }
    }
} 