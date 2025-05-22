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

    private final ScopeRepository scopeRepository;
    private final RoleRepository roleRepository;
    private final ResourceDiscoveryService resourceDiscoveryService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final ResourceRepository resourceRepository;
    private final PolicyRepository policyRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // Create scopes first
        createScopes();
        
        // Discover and register resources
        discoverAndRegisterResources();
        
        // Create roles and assign scopes
        createRolesAndAssignScopes();
        
        // Create policies for roles
        createPolicies();
        
        // Create admin user if not exists
        createAdminUser();
    }

    private void createScopes() {
        // User related scopes
        createScopeIfNotExists("read:user", "Allows reading user info");
        createScopeIfNotExists("write:user", "Allows creating and updating user info");
        createScopeIfNotExists("delete:user", "Allows deleting a user");
        createScopeIfNotExists("list:users", "Allows listing all users");
        
        // Role related scopes
        createScopeIfNotExists("read:role", "Allows reading role info");
        createScopeIfNotExists("create:role", "Allows creating new role");
        createScopeIfNotExists("update:role", "Allows updating role info");
        createScopeIfNotExists("delete:role", "Allows deleting a role");
        createScopeIfNotExists("list:roles", "Allows listing all roles");
        
        // Scope related scopes
        createScopeIfNotExists("read:scope", "Allows reading scope info");
        createScopeIfNotExists("create:scope", "Allows creating new scope");
        createScopeIfNotExists("update:scope", "Allows updating scope info");
        createScopeIfNotExists("delete:scope", "Allows deleting a scope");
        createScopeIfNotExists("list:scopes", "Allows listing all scopes");
        
        // Resource related scopes
        createScopeIfNotExists("read:resource", "Read protected resources");
        createScopeIfNotExists("write:resource", "Create and update protected resources");
        createScopeIfNotExists("delete:resource", "Delete protected resources");
        createScopeIfNotExists("list:resources", "List all protected resources");
        
        // Policy related scopes
        createScopeIfNotExists("read:policy", "Read access policies");
        createScopeIfNotExists("write:policy", "Create and update access policies");
        createScopeIfNotExists("delete:policy", "Delete access policies");
        createScopeIfNotExists("list:policies", "List all access policies");
        
        // System management scopes
        createScopeIfNotExists("manage:system", "Full system management access");
        createScopeIfNotExists("audit:logs", "Access to audit logs");
        createScopeIfNotExists("monitor:system", "System monitoring access");
    }
    
    private void discoverAndRegisterResources() {
        resourceDiscoveryService.discoverAndRegisterResources();
    }

    private void createScopeIfNotExists(String name, String description) {
        if (!scopeRepository.existsByName(name)) {
            Scope scope = new Scope();
            scope.setName(name);
            scope.setDescription(description);
            scopeRepository.save(scope);
        }
    }

    private void createRolesAndAssignScopes() {
        // Create ADMIN role with all scopes
        Role admin = createRoleIfNotExists("ADMIN", "Administrator with full access");
        Set<Scope> allScopes = new HashSet<>(scopeRepository.findAll());
        admin.setScopes(allScopes);
        roleRepository.save(admin);

        // Create USER role with limited scopes
        Role user = createRoleIfNotExists("USER", "Normal user with limited access");
        Set<Scope> userScopes = allScopes.stream()
                .filter(s -> s.getName().startsWith("read:"))
                .collect(Collectors.toSet());
        user.setScopes(userScopes);
        roleRepository.save(user);

        // Create MANAGER role with more permissions
        Role manager = createRoleIfNotExists("MANAGER", "Manager with elevated access");
        Set<Scope> managerScopes = allScopes.stream()
                .filter(s -> s.getName().startsWith("read:") || 
                           s.getName().startsWith("write:") ||
                           s.getName().startsWith("list:"))
                .collect(Collectors.toSet());
        manager.setScopes(managerScopes);
        roleRepository.save(manager);

        // Create EDITOR role for content management
        Role editor = createRoleIfNotExists("EDITOR", "Content editor with write access");
        Set<Scope> editorScopes = allScopes.stream()
                .filter(s -> s.getName().startsWith("read:") || 
                           s.getName().startsWith("write:") ||
                           s.getName().equals("list:resources"))
                .collect(Collectors.toSet());
        editor.setScopes(editorScopes);
        roleRepository.save(editor);

        // Create VIEWER role for read-only access
        Role viewer = createRoleIfNotExists("VIEWER", "Viewer with read-only access");
        Set<Scope> viewerScopes = allScopes.stream()
                .filter(s -> s.getName().startsWith("read:") || 
                           s.getName().startsWith("list:"))
                .collect(Collectors.toSet());
        viewer.setScopes(viewerScopes);
        roleRepository.save(viewer);
    }

    private Role createRoleIfNotExists(String name, String description) {
        return roleRepository.findByName(name).orElseGet(() -> {
            Role role = new Role();
            role.setName(name);
            role.setDescription(description);
            return roleRepository.save(role);
        });
    }

    private void createPolicies() {
        // Get all resources
        List<Resource> resources = resourceRepository.findAll();
        
        // Get roles
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("Admin role not found"));
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("User role not found"));
        Role managerRole = roleRepository.findByName("MANAGER")
                .orElseThrow(() -> new RuntimeException("Manager role not found"));
        Role editorRole = roleRepository.findByName("EDITOR")
                .orElseThrow(() -> new RuntimeException("Editor role not found"));
        Role viewerRole = roleRepository.findByName("VIEWER")
                .orElseThrow(() -> new RuntimeException("Viewer role not found"));

        // Get scopes
        Set<Scope> allScopes = new HashSet<>(scopeRepository.findAll());
        Set<Scope> readScopes = allScopes.stream()
                .filter(s -> s.getName().startsWith("read:"))
                .collect(Collectors.toSet());
        Set<Scope> writeScopes = allScopes.stream()
                .filter(s -> s.getName().startsWith("write:"))
                .collect(Collectors.toSet());
        Set<Scope> listScopes = allScopes.stream()
                .filter(s -> s.getName().startsWith("list:"))
                .collect(Collectors.toSet());

        // Create policies for each role
        for (Resource resource : resources) {
            // Admin has full access to all resources
            createPolicyIfNotExists(adminRole, resource, allScopes, Policy.SubjectType.ROLE);

            // Manager has read, write, and list access
            Set<Scope> managerScopes = new HashSet<>();
            managerScopes.addAll(readScopes);
            managerScopes.addAll(writeScopes);
            managerScopes.addAll(listScopes);
            createPolicyIfNotExists(managerRole, resource, managerScopes, Policy.SubjectType.ROLE);

            // Editor has read and write access
            Set<Scope> editorScopes = new HashSet<>();
            editorScopes.addAll(readScopes);
            editorScopes.addAll(writeScopes);
            createPolicyIfNotExists(editorRole, resource, editorScopes, Policy.SubjectType.ROLE);

            // Viewer has read and list access
            Set<Scope> viewerScopes = new HashSet<>();
            viewerScopes.addAll(readScopes);
            viewerScopes.addAll(listScopes);
            createPolicyIfNotExists(viewerRole, resource, viewerScopes, Policy.SubjectType.ROLE);

            // Regular user has only read access
            createPolicyIfNotExists(userRole, resource, readScopes, Policy.SubjectType.ROLE);
        }
    }

    private void createPolicyIfNotExists(Role role, Resource resource, Set<Scope> scopes, Policy.SubjectType subjectType) {
        for (Scope scope : scopes) {
            Policy policy = new Policy();
            policy.setSubjectId(role.getId());
            policy.setSubjectType(subjectType);
            policy.setResource(resource);
            policy.setScope(scope);
            policy.setDescription(String.format("Policy for %s to access %s with scope %s", 
                role.getName(), resource.getName(), scope.getName()));
            policyRepository.save(policy);
        }
    }

    private void createAdminUser() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123")); // Change this in production
            admin.setFullName("Administrator");
            
            // Get admin role
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));
            
            // Assign admin role
            admin.setRoles(Set.of(adminRole));
            
            // Save admin user
            userRepository.save(admin);
            log.info("Created admin user with username: admin");
        }
    }
}
