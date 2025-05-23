package com.example.iam.config;

import com.example.iam.entity.Resource;
import com.example.iam.entity.Role;
import com.example.iam.entity.Scope;
import com.example.iam.repository.ResourceRepository;
import com.example.iam.repository.RoleRepository;
import com.example.iam.repository.ScopeRepository;
import com.example.iam.service.ResourceDiscoveryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.example.iam.entity.User;
import com.example.iam.repository.UserRepository;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import com.example.iam.entity.Policy;
import com.example.iam.repository.PolicyRepository;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ScopeRepository scopeRepository;
    private final ResourceRepository resourceRepository;
    private final PasswordEncoder passwordEncoder;
    private final ResourceDiscoveryService resourceDiscoveryService;
    private final PolicyRepository policyRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("Initializing data...");

        // Initialize scopes
        initializeScopes();

        // Initialize roles
        initializeRoles();

        // Initialize resources and their scope mappings
        initializeResources();

        // Initialize admin user
        initializeAdminUser();

        // Initialize policies
        initializePolicies();

        log.info("Data initialization completed.");
    }

    private void initializeScopes() {
        log.info("Initializing scopes...");

        // User management scopes
        createScopeIfNotExists("read:users", "Read user information");
        createScopeIfNotExists("write:users", "Create and update users");
        createScopeIfNotExists("delete:users", "Delete users");
        createScopeIfNotExists("list:users", "List all users");

        // Role management scopes
        createScopeIfNotExists("read:roles", "Read role information");
        createScopeIfNotExists("write:roles", "Create and update roles");
        createScopeIfNotExists("delete:roles", "Delete roles");
        createScopeIfNotExists("list:roles", "List all roles");

        // Policy management scopes
        createScopeIfNotExists("read:policies", "Read policy information");
        createScopeIfNotExists("write:policies", "Create and update policies");
        createScopeIfNotExists("delete:policies", "Delete policies");
        createScopeIfNotExists("list:policies", "List all policies");

        // Client management scopes
        createScopeIfNotExists("read:clients", "Read client information");
        createScopeIfNotExists("write:clients", "Create and update clients");
        createScopeIfNotExists("delete:clients", "Delete clients");
        createScopeIfNotExists("list:clients", "List all clients");

        // Resource management scopes
        createScopeIfNotExists("read:resources", "Read resource information");
        createScopeIfNotExists("write:resources", "Create and update resources");
        createScopeIfNotExists("delete:resources", "Delete resources");
        createScopeIfNotExists("list:resources", "List all resources");

        // Scope management scopes
        createScopeIfNotExists("read:scopes", "Read scope information");
        createScopeIfNotExists("write:scopes", "Create and update scopes");
        createScopeIfNotExists("delete:scopes", "Delete scopes");
        createScopeIfNotExists("list:scopes", "List all scopes");
    }

    private void initializeRoles() {
        log.info("Initializing roles...");

        // Admin role with all scopes
        Role adminRole = createRoleIfNotExists("ROLE_ADMIN", "Administrator with full access");
        Set<Scope> allScopes = new HashSet<>(scopeRepository.findAll());
        adminRole.setScopes(allScopes);
        roleRepository.save(adminRole);

        // User role with basic scopes
        Role userRole = createRoleIfNotExists("ROLE_USER", "Regular user with basic access");
        Set<Scope> userScopes = scopeRepository.findAll().stream()
                .filter(scope -> scope.getName().startsWith("read:") || 
                               scope.getName().startsWith("list:"))
                .collect(Collectors.toSet());
        userRole.setScopes(userScopes);
        roleRepository.save(userRole);
    }

    private void initializeResources() {
        log.info("Initializing resources...");

        // Define base paths
        String[] basePaths = {""};

        // User management resources
        for (String basePath : basePaths) {
            // List users
            createResourceWithScopes(
                basePath + "/users",
                "Users API",
                "User management endpoints",
                Resource.HttpMethod.GET,
                Set.of("read:users", "list:users")
            );

            // Get user by ID
            createResourceWithScopes(
                basePath + "/users/{id}",
                "User Detail API",
                "User detail endpoints",
                Resource.HttpMethod.GET,
                Set.of("read:users")
            );

            // Create user
            createResourceWithScopes(
                basePath + "/users",
                "Create User API",
                "Create user endpoint",
                Resource.HttpMethod.POST,
                Set.of("write:users")
            );

            // Update user
            createResourceWithScopes(
                basePath + "/users/{id}",
                "Update User API",
                "Update user endpoint",
                Resource.HttpMethod.PUT,
                Set.of("write:users")
            );

            // Delete user
            createResourceWithScopes(
                basePath + "/users/{id}",
                "Delete User API",
                "Delete user endpoint",
                Resource.HttpMethod.DELETE,
                Set.of("delete:users")
            );
        }

        // Role management resources
        for (String basePath : basePaths) {
            // List roles
            createResourceWithScopes(
                basePath + "/roles",
                "Roles API",
                "Role management endpoints",
                Resource.HttpMethod.GET,
                Set.of("read:roles", "list:roles")
            );

            // Get role by ID
            createResourceWithScopes(
                basePath + "/roles/{id}",
                "Role Detail API",
                "Role detail endpoints",
                Resource.HttpMethod.GET,
                Set.of("read:roles")
            );

            // Create role
            createResourceWithScopes(
                basePath + "/roles",
                "Create Role API",
                "Create role endpoint",
                Resource.HttpMethod.POST,
                Set.of("write:roles")
            );

            // Update role
            createResourceWithScopes(
                basePath + "/roles/{id}",
                "Update Role API",
                "Update role endpoint",
                Resource.HttpMethod.PUT,
                Set.of("write:roles")
            );

            // Delete role
            createResourceWithScopes(
                basePath + "/roles/{id}",
                "Delete Role API",
                "Delete role endpoint",
                Resource.HttpMethod.DELETE,
                Set.of("delete:roles")
            );
        }

        // Policy management resources
        for (String basePath : basePaths) {
            // List policies
            createResourceWithScopes(
                basePath + "/policies",
                "Policies API",
                "Policy management endpoints",
                Resource.HttpMethod.GET,
                Set.of("read:policies", "list:policies")
            );

            // Get policy by ID
            createResourceWithScopes(
                basePath + "/policies/{id}",
                "Policy Detail API",
                "Policy detail endpoints",
                Resource.HttpMethod.GET,
                Set.of("read:policies")
            );

            // Create policy
            createResourceWithScopes(
                basePath + "/policies",
                "Create Policy API",
                "Create policy endpoint",
                Resource.HttpMethod.POST,
                Set.of("write:policies")
            );

            // Update policy
            createResourceWithScopes(
                basePath + "/policies/{id}",
                "Update Policy API",
                "Update policy endpoint",
                Resource.HttpMethod.PUT,
                Set.of("write:policies")
            );

            // Delete policy
            createResourceWithScopes(
                basePath + "/policies/{id}",
                "Delete Policy API",
                "Delete policy endpoint",
                Resource.HttpMethod.DELETE,
                Set.of("delete:policies")
            );
        }

        // Client management resources
        for (String basePath : basePaths) {
            // List clients
            createResourceWithScopes(
                basePath + "/clients",
                "Clients API",
                "Client management endpoints",
                Resource.HttpMethod.GET,
                Set.of("read:clients", "list:clients")
            );

            // Get client by ID
            createResourceWithScopes(
                basePath + "/clients/{id}",
                "Client Detail API",
                "Client detail endpoints",
                Resource.HttpMethod.GET,
                Set.of("read:clients")
            );

            // Create client
            createResourceWithScopes(
                basePath + "/clients",
                "Create Client API",
                "Create client endpoint",
                Resource.HttpMethod.POST,
                Set.of("write:clients")
            );

            // Update client
            createResourceWithScopes(
                basePath + "/clients/{id}",
                "Update Client API",
                "Update client endpoint",
                Resource.HttpMethod.PUT,
                Set.of("write:clients")
            );

            // Delete client
            createResourceWithScopes(
                basePath + "/clients/{id}",
                "Delete Client API",
                "Delete client endpoint",
                Resource.HttpMethod.DELETE,
                Set.of("delete:clients")
            );
        }

        // Resource management resources
        for (String basePath : basePaths) {
            // List resources
            createResourceWithScopes(
                basePath + "/resources",
                "Resources API",
                "Resource management endpoints",
                Resource.HttpMethod.GET,
                Set.of("read:resources", "list:resources")
            );

            // Get resource by ID
            createResourceWithScopes(
                basePath + "/resources/{id}",
                "Resource Detail API",
                "Resource detail endpoints",
                Resource.HttpMethod.GET,
                Set.of("read:resources")
            );

            // Create resource
            createResourceWithScopes(
                basePath + "/resources",
                "Create Resource API",
                "Create resource endpoint",
                Resource.HttpMethod.POST,
                Set.of("write:resources")
            );

            // Update resource
            createResourceWithScopes(
                basePath + "/resources/{id}",
                "Update Resource API",
                "Update resource endpoint",
                Resource.HttpMethod.PUT,
                Set.of("write:resources")
            );

            // Delete resource
            createResourceWithScopes(
                basePath + "/resources/{id}",
                "Delete Resource API",
                "Delete resource endpoint",
                Resource.HttpMethod.DELETE,
                Set.of("delete:resources")
            );
        }

        // Scope management resources
        for (String basePath : basePaths) {
            // List scopes
            createResourceWithScopes(
                basePath + "/scopes",
                "Scopes API",
                "Scope management endpoints",
                Resource.HttpMethod.GET,
                Set.of("read:scopes", "list:scopes")
            );

            // Get scope by ID
            createResourceWithScopes(
                basePath + "/scopes/{id}",
                "Scope Detail API",
                "Scope detail endpoints",
                Resource.HttpMethod.GET,
                Set.of("read:scopes")
            );

            // Create scope
            createResourceWithScopes(
                basePath + "/scopes",
                "Create Scope API",
                "Create scope endpoint",
                Resource.HttpMethod.POST,
                Set.of("write:scopes")
            );

            // Update scope
            createResourceWithScopes(
                basePath + "/scopes/{id}",
                "Update Scope API",
                "Update scope endpoint",
                Resource.HttpMethod.PUT,
                Set.of("write:scopes")
            );

            // Delete scope
            createResourceWithScopes(
                basePath + "/scopes/{id}",
                "Delete Scope API",
                "Delete scope endpoint",
                Resource.HttpMethod.DELETE,
                Set.of("delete:scopes")
            );
        }
    }

    private void initializeAdminUser() {
        log.info("Initializing admin user...");

        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@example.com");
            admin.setFullName("System Administrator");
            admin.setEnabled(true);

            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));
            admin.setRoles(Set.of(adminRole));

            userRepository.save(admin);
            log.info("Admin user created successfully");
        }
    }

    private void initializePolicies() {
        log.info("Initializing policies...");

        // Get admin user and role
        User admin = userRepository.findByUsername("admin")
                .orElseThrow(() -> new RuntimeException("Admin user not found"));
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("Admin role not found"));

        // Get all resources
        List<Resource> allResources = resourceRepository.findAll();

        // Create user-specific policies for admin
        for (Resource resource : allResources) {
            // Create policy for admin user
            Policy userPolicy = new Policy();
            userPolicy.setSubjectId(admin.getId());
            userPolicy.setSubjectType(Policy.SubjectType.USER);
            userPolicy.setResource(resource);
            userPolicy.setScope(resource.getScopes().iterator().next()); // Use first scope
            userPolicy.setDescription("Admin user policy for " + resource.getPath());
            policyRepository.save(userPolicy);

            // Create policy for admin role
            Policy rolePolicy = new Policy();
            rolePolicy.setSubjectId(adminRole.getId());
            rolePolicy.setSubjectType(Policy.SubjectType.ROLE);
            rolePolicy.setResource(resource);
            rolePolicy.setScope(resource.getScopes().iterator().next()); // Use first scope
            rolePolicy.setDescription("Admin role policy for " + resource.getPath());
            policyRepository.save(rolePolicy);
        }

        log.info("Policies initialized successfully");
    }

    private void createScopeIfNotExists(String name, String description) {
        if (scopeRepository.findByName(name).isEmpty()) {
            Scope scope = new Scope();
            scope.setName(name);
            scope.setDescription(description);
            scopeRepository.save(scope);
            log.debug("Created scope: {}", name);
        }
    }

    private Role createRoleIfNotExists(String name, String description) {
        return roleRepository.findByName(name)
                .orElseGet(() -> {
            Role role = new Role();
            role.setName(name);
            role.setDescription(description);
            return roleRepository.save(role);
        });
    }

    private void createResourceWithScopes(String path, String name, String description,
                                        Resource.HttpMethod method, Set<String> scopeNames) {
        // Check if resource with same path and method already exists
        if (resourceRepository.findByPathAndMethod(path, method).isPresent()) {
            log.debug("Resource already exists: {} [{}]", path, method);
            return;
        }

        Resource resource = new Resource();
        resource.setPath(path);
        resource.setName(name);
        resource.setDescription(description);
        resource.setMethod(method);

        Set<Scope> scopes = scopeNames.stream()
                .map(scopeName -> scopeRepository.findByName(scopeName)
                        .orElseThrow(() -> new RuntimeException("Scope not found: " + scopeName)))
                .collect(Collectors.toSet());
        resource.setScopes(scopes);

        resourceRepository.save(resource);
        log.debug("Created resource: {} [{}] with scopes: {}", path, method, scopeNames);
    }
}
