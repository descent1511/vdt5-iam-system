package com.example.iam.config;

import com.example.iam.entity.*;
import com.example.iam.repository.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final PermissionRepository permissionRepository;
    private final ScopeRepository scopeRepository;
    private final RoleRepository roleRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.base-url:}")
    private String baseUrl;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    @Value("${app.admin.email:admin@example.com}")
    private String adminEmail;

    @PostConstruct
    @Transactional
    public void init() {
        // Initialize Permissions
        Set<Permission> permissions = createPermissions();
        
        // Initialize Scopes
        Set<Scope> scopes = createScopes(permissions);
        
        // Initialize Resources with Permissions
        createResources(permissions);
        
        // Initialize Roles
        createRoles(permissions);

        // Initialize Admin User
        createAdminUser();
    }

    private void createAdminUser() {
        userRepository.findByUsername(adminUsername)
            .orElseGet(() -> {
                User admin = new User();
                admin.setUsername(adminUsername);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setEmail(adminEmail);
                admin.setEnabled(true);
                admin.setFullName("System Administrator");

                // Set admin role
                Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));
                admin.addRole(adminRole);

                // Set admin scope
                Scope adminScope = scopeRepository.findByName("admin")
                    .orElseThrow(() -> new RuntimeException("Admin scope not found"));
                admin.addScope(adminScope);

                return userRepository.save(admin);
            });
    }

    private Set<Permission> createPermissions() {
        Set<Permission> permissions = new HashSet<>();
        
        // User Management Permissions
        permissions.add(createPermission("USER_CREATE", "Create new user"));
        permissions.add(createPermission("USER_READ", "View user details"));
        permissions.add(createPermission("USER_UPDATE", "Update user information"));
        permissions.add(createPermission("USER_DELETE", "Delete user"));
        
        // Role Management Permissions
        permissions.add(createPermission("ROLE_CREATE", "Create new role"));
        permissions.add(createPermission("ROLE_READ", "View role details"));
        permissions.add(createPermission("ROLE_UPDATE", "Update role information"));
        permissions.add(createPermission("ROLE_DELETE", "Delete role"));
        
        // Resource Management Permissions
        permissions.add(createPermission("RESOURCE_CREATE", "Create new resource"));
        permissions.add(createPermission("RESOURCE_READ", "View resource details"));
        permissions.add(createPermission("RESOURCE_UPDATE", "Update resource information"));
        permissions.add(createPermission("RESOURCE_DELETE", "Delete resource"));
        
        // Client Application Permissions
        permissions.add(createPermission("CLIENT_CREATE", "Create new client application"));
        permissions.add(createPermission("CLIENT_READ", "View client details"));
        permissions.add(createPermission("CLIENT_UPDATE", "Update client information"));
        permissions.add(createPermission("CLIENT_DELETE", "Delete client application"));

        return permissions;
    }

    private Set<Scope> createScopes(Set<Permission> permissions) {
        Set<Scope> scopes = new HashSet<>();
        
        // Admin Scope with all permissions
        Scope adminScope = scopeRepository.findByName("admin")
            .orElseGet(() -> {
                Scope scope = new Scope();
                scope.setName("admin");
                scope.setDescription("Full administrative access");
                scope.setPermissions(permissions);
                return scopeRepository.save(scope);
            });
        scopes.add(adminScope);
        
        // User Management Scope
        Scope userManagementScope = scopeRepository.findByName("user_management")
            .orElseGet(() -> {
                Scope scope = new Scope();
                scope.setName("user_management");
                scope.setDescription("User management access");
                scope.setPermissions(permissions.stream()
                    .filter(p -> p.getName().startsWith("USER_"))
                    .collect(java.util.stream.Collectors.toSet()));
                return scopeRepository.save(scope);
            });
        scopes.add(userManagementScope);
        
        // Basic User Scope
        Scope basicUserScope = scopeRepository.findByName("basic_user")
            .orElseGet(() -> {
                Scope scope = new Scope();
                scope.setName("basic_user");
                scope.setDescription("Basic user access");
                scope.setPermissions(permissions.stream()
                    .filter(p -> p.getName().equals("USER_READ"))
                    .collect(java.util.stream.Collectors.toSet()));
                return scopeRepository.save(scope);
            });
        scopes.add(basicUserScope);

        return scopes;
    }

    private void createResources(Set<Permission> permissions) {
        // User Resources
        createResource("user_list", baseUrl + "/users", "GET", "List all users",
            permissions.stream().filter(p -> p.getName().equals("USER_READ")).collect(java.util.stream.Collectors.toSet()));
        
        createResource("user_detail", baseUrl + "/users/{id}", "GET", "Get user details",
            permissions.stream().filter(p -> p.getName().equals("USER_READ")).collect(java.util.stream.Collectors.toSet()));
        
        createResource("user_create", baseUrl + "/users", "POST", "Create new user",
            permissions.stream().filter(p -> p.getName().equals("USER_CREATE")).collect(java.util.stream.Collectors.toSet()));
        
        createResource("user_update", baseUrl + "/users/{id}", "PUT", "Update user",
            permissions.stream().filter(p -> p.getName().equals("USER_UPDATE")).collect(java.util.stream.Collectors.toSet()));
        
        createResource("user_delete", baseUrl + "/users/{id}", "DELETE", "Delete user",
            permissions.stream().filter(p -> p.getName().equals("USER_DELETE")).collect(java.util.stream.Collectors.toSet()));

        // Role Resources
        createResource("role_list", baseUrl + "/roles", "GET", "List all roles",
            permissions.stream().filter(p -> p.getName().equals("ROLE_READ")).collect(java.util.stream.Collectors.toSet()));
        
        createResource("role_detail", baseUrl + "/roles/{id}", "GET", "Get role details",
            permissions.stream().filter(p -> p.getName().equals("ROLE_READ")).collect(java.util.stream.Collectors.toSet()));
        
        createResource("role_create", baseUrl + "/roles", "POST", "Create new role",
            permissions.stream().filter(p -> p.getName().equals("ROLE_CREATE")).collect(java.util.stream.Collectors.toSet()));
        
        createResource("role_update", baseUrl + "/roles/{id}", "PUT", "Update role",
            permissions.stream().filter(p -> p.getName().equals("ROLE_UPDATE")).collect(java.util.stream.Collectors.toSet()));
        
        createResource("role_delete", baseUrl + "/roles/{id}", "DELETE", "Delete role",
            permissions.stream().filter(p -> p.getName().equals("ROLE_DELETE")).collect(java.util.stream.Collectors.toSet()));

        // Resource Management Resources
        createResource("resource_list", baseUrl + "/resources", "GET", "List all resources",
            permissions.stream().filter(p -> p.getName().equals("RESOURCE_READ")).collect(java.util.stream.Collectors.toSet()));
        
        createResource("resource_detail", baseUrl + "/resources/{id}", "GET", "Get resource details",
            permissions.stream().filter(p -> p.getName().equals("RESOURCE_READ")).collect(java.util.stream.Collectors.toSet()));
        
        createResource("resource_create", baseUrl + "/resources", "POST", "Create new resource",
            permissions.stream().filter(p -> p.getName().equals("RESOURCE_CREATE")).collect(java.util.stream.Collectors.toSet()));
        
        createResource("resource_update", baseUrl + "/resources/{id}", "PUT", "Update resource",
            permissions.stream().filter(p -> p.getName().equals("RESOURCE_UPDATE")).collect(java.util.stream.Collectors.toSet()));
        
        createResource("resource_delete", baseUrl + "/resources/{id}", "DELETE", "Delete resource",
            permissions.stream().filter(p -> p.getName().equals("RESOURCE_DELETE")).collect(java.util.stream.Collectors.toSet()));

        // Client Resources
        createResource("client_list", baseUrl + "/clients", "GET", "List all clients",
            permissions.stream().filter(p -> p.getName().equals("CLIENT_READ")).collect(java.util.stream.Collectors.toSet()));
        
        createResource("client_detail", baseUrl + "/clients/{id}", "GET", "Get client details",
            permissions.stream().filter(p -> p.getName().equals("CLIENT_READ")).collect(java.util.stream.Collectors.toSet()));
        
        createResource("client_create", baseUrl + "/clients", "POST", "Create new client",
            permissions.stream().filter(p -> p.getName().equals("CLIENT_CREATE")).collect(java.util.stream.Collectors.toSet()));
        
        createResource("client_update", baseUrl + "/clients/{id}", "PUT", "Update client",
            permissions.stream().filter(p -> p.getName().equals("CLIENT_UPDATE")).collect(java.util.stream.Collectors.toSet()));
        
        createResource("client_delete", baseUrl + "/clients/{id}", "DELETE", "Delete client",
            permissions.stream().filter(p -> p.getName().equals("CLIENT_DELETE")).collect(java.util.stream.Collectors.toSet()));
    }

    private Resource createResource(String name, String path, String method, String description, Set<Permission> permissions) {
        return resourceRepository.findByPathAndMethod(path, Resource.HttpMethod.valueOf(method))
            .orElseGet(() -> {
                Resource resource = new Resource();
                resource.setName(name);
                resource.setPath(path);
                resource.setDescription(description);
                resource.setMethod(Resource.HttpMethod.valueOf(method));
                resource.setPermissions(permissions);
                return resourceRepository.save(resource);
            });
    }

    private void createRoles(Set<Permission> permissions) {
        // Admin Role - có tất cả permissions
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
            .orElseGet(() -> {
                Role role = new Role();
                role.setName("ROLE_ADMIN");
                role.setDescription("Administrator role with full access");
                role.setPermissions(permissions);
                return roleRepository.save(role);
            });
        
        // User Management Role - chỉ có permissions liên quan đến user
        Role userManagerRole = roleRepository.findByName("ROLE_USER_MANAGER")
            .orElseGet(() -> {
                Role role = new Role();
                role.setName("ROLE_USER_MANAGER");
                role.setDescription("User management role");
                role.setPermissions(permissions.stream()
                    .filter(p -> p.getName().startsWith("USER_"))
                    .collect(java.util.stream.Collectors.toSet()));
                return roleRepository.save(role);
            });
        
        // Basic User Role - chỉ có quyền đọc
        Role basicUserRole = roleRepository.findByName("ROLE_USER")
            .orElseGet(() -> {
                Role role = new Role();
                role.setName("ROLE_USER");
                role.setDescription("Basic user role");
                role.setPermissions(permissions.stream()
                    .filter(p -> p.getName().equals("USER_READ"))
                    .collect(java.util.stream.Collectors.toSet()));
                return roleRepository.save(role);
            });
    }

    private Permission createPermission(String name, String description) {
        return permissionRepository.findByName(name)
            .orElseGet(() -> {
                Permission permission = new Permission();
                permission.setName(name);
                permission.setDescription(description);
                return permissionRepository.save(permission);
            });
    }
} 