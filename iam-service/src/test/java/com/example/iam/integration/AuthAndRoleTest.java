package com.example.iam.integration;

import com.example.iam.dto.LoginRequest;
import com.example.iam.dto.SignupRequest;
import com.example.iam.dto.TokenResponse;
import com.example.iam.entity.*;
import com.example.iam.repository.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthAndRoleTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private ResourceRepository resourceRepository;


    @Autowired
    private EntityManager entityManager;
    
    private String baseUrl;
    private HttpHeaders headers;
    private String adminToken;
    private String managerToken;
    private String userToken;

    @BeforeEach
    @Transactional
    void setUp() {
        baseUrl = "http://localhost:" + port;
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create User permissions
        Permission userRead = createPermission("USER_READ", "Read user information");
        Permission userWrite = createPermission("USER_WRITE", "Create/Update user");
        Permission userDelete = createPermission("USER_DELETE", "Delete user");
        Permission userList = createPermission("USER_LIST", "List all users");

        // Create Role permissions
        Permission roleRead = createPermission("ROLE_READ", "Read role information");
        Permission roleWrite = createPermission("ROLE_WRITE", "Create/Update role");
        Permission roleDelete = createPermission("ROLE_DELETE", "Delete role");
        Permission roleList = createPermission("ROLE_LIST", "List all roles");

        // Create Resource permissions
        Permission resourceRead = createPermission("RESOURCE_READ", "Read resource information");
        Permission resourceWrite = createPermission("RESOURCE_WRITE", "Create/Update resource");
        Permission resourceDelete = createPermission("RESOURCE_DELETE", "Delete resource");
        Permission resourceList = createPermission("RESOURCE_LIST", "List all resources");

        // Create Organization permissions
        Permission orgRead = createPermission("ORG_READ", "Read organization information");
        Permission orgWrite = createPermission("ORG_WRITE", "Create/Update organization");
        Permission orgDelete = createPermission("ORG_DELETE", "Delete organization");
        Permission orgList = createPermission("ORG_LIST", "List all organizations");

        // Create Policy permissions
        Permission policyRead = createPermission("POLICY_READ", "Read policy information");
        Permission policyWrite = createPermission("POLICY_WRITE", "Create/Update policy");
        Permission policyDelete = createPermission("POLICY_DELETE", "Delete policy");
        Permission policyList = createPermission("POLICY_LIST", "List all policies");

        // Create roles with permissions
        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        adminRole.setPermissions(Set.of(
            userRead, userWrite, userDelete, userList,
            roleRead, roleWrite, roleDelete, roleList,
            resourceRead, resourceWrite, resourceDelete, resourceList,
            orgRead, orgWrite, orgDelete, orgList,
            policyRead, policyWrite, policyDelete, policyList
        ));
        roleRepository.save(adminRole);

        Role managerRole = new Role();
        managerRole.setName("ROLE_MANAGER");
        managerRole.setPermissions(Set.of(
            userRead, userWrite, userList,
            roleRead, roleList,
            resourceRead, resourceList,
            orgRead, orgList,
            policyRead, policyList
        ));
        roleRepository.save(managerRole);

        Role userRole = new Role();
        userRole.setName("ROLE_USER");
        userRole.setPermissions(Set.of(
            userRead,
            roleRead,
            resourceRead,
            orgRead,
            policyRead
        ));
        roleRepository.save(userRole);

        // Create resources with permissions
        // User resources
        createResource("/users", Resource.HttpMethod.GET, Set.of(userList));
        createResource("/users/{id}", Resource.HttpMethod.GET, Set.of(userRead));
        createResource("/users", Resource.HttpMethod.POST, Set.of(userWrite));
        createResource("/users/{id}", Resource.HttpMethod.PUT, Set.of(userWrite));
        createResource("/users/{id}", Resource.HttpMethod.DELETE, Set.of(userDelete));

        // Role resources
        createResource("/roles", Resource.HttpMethod.GET, Set.of(roleList));
        createResource("/roles/{id}", Resource.HttpMethod.GET, Set.of(roleRead));
        createResource("/roles", Resource.HttpMethod.POST, Set.of(roleWrite));
        createResource("/roles/{id}", Resource.HttpMethod.PUT, Set.of(roleWrite));
        createResource("/roles/{id}", Resource.HttpMethod.DELETE, Set.of(roleDelete));

        // Resource resources
        createResource("/resources", Resource.HttpMethod.GET, Set.of(resourceList));
        createResource("/resources/{id}", Resource.HttpMethod.GET, Set.of(resourceRead));
        createResource("/resources", Resource.HttpMethod.POST, Set.of(resourceWrite));
        createResource("/resources/{id}", Resource.HttpMethod.PUT, Set.of(resourceWrite));
        createResource("/resources/{id}", Resource.HttpMethod.DELETE, Set.of(resourceDelete));

        // Organization resources
        createResource("/organizations", Resource.HttpMethod.GET, Set.of(orgList));
        createResource("/organizations/{id}", Resource.HttpMethod.GET, Set.of(orgRead));
        createResource("/organizations", Resource.HttpMethod.POST, Set.of(orgWrite));
        createResource("/organizations/{id}", Resource.HttpMethod.PUT, Set.of(orgWrite));
        createResource("/organizations/{id}", Resource.HttpMethod.DELETE, Set.of(orgDelete));

        // Policy resources
        createResource("/policies", Resource.HttpMethod.GET, Set.of(policyList));
        createResource("/policies/{id}", Resource.HttpMethod.GET, Set.of(policyRead));
        createResource("/policies", Resource.HttpMethod.POST, Set.of(policyWrite));
        createResource("/policies/{id}", Resource.HttpMethod.PUT, Set.of(policyWrite));
        createResource("/policies/{id}", Resource.HttpMethod.DELETE, Set.of(policyDelete));

        // Create users with different roles
        createUser("admin", "admin123", "admin@example.com", "Admin User", adminRole);
        createUser("manager", "manager123", "manager@example.com", "Manager User", managerRole);
        createUser("user", "user123", "user@example.com", "Regular User", userRole);

        // Get tokens
        adminToken = getToken("admin", "admin123");
        managerToken = getToken("manager", "manager123");
        userToken = getToken("user", "user123");

        // Flush and clear persistence context
        entityManager.flush();
        entityManager.clear();
    }

    @Transactional
    private Permission createPermission(String name, String description) {
        return permissionRepository.findByName(name)
            .orElseGet(() -> {
                Permission permission = new Permission();
                permission.setName(name);
                permission.setDescription(description);
                return permissionRepository.save(permission);
            });
    }

    @Transactional
    private Resource createResource(String path, Resource.HttpMethod method, Set<Permission> permissions) {
        Resource resource = new Resource();
        resource.setPath(path);
        resource.setMethod(method);
        resource.setName(method.name() + "_" + path.replaceAll("[^a-zA-Z0-9]", "_"));
        return resourceRepository.save(resource);
    }

    @Transactional
    private void createUser(String username, String password, String email, String fullName, Role role) {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername(username);
        signupRequest.setPassword(password);
        signupRequest.setEmail(email);
        signupRequest.setFullName(fullName);

        restTemplate.postForEntity(
            baseUrl + "/auth/signup",
            new HttpEntity<>(signupRequest, headers),
            TokenResponse.class
        );
    }

    private String getToken(String username, String password) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
            baseUrl + "/auth/login",
            new HttpEntity<>(loginRequest, headers),
            TokenResponse.class
        );
        return response.getBody().getAccessToken();
    }

    // Test cases for User endpoints
    @Test
    @Transactional
    void testAdminCanListUsers() {
        headers.setBearerAuth(adminToken);
        ResponseEntity<User[]> response = restTemplate.exchange(
            baseUrl + "/users",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            User[].class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // @Test
    // @Transactional
    // void testManagerCanListUsers() {
    //     headers.setBearerAuth(managerToken);
    //     ResponseEntity<User[]> response = restTemplate.exchange(
    //         baseUrl + "/users",
    //         HttpMethod.GET,
    //         new HttpEntity<>(headers),
    //         User[].class
    //     );
    //     assertEquals(HttpStatus.OK, response.getStatusCode());
    // }

    // @Test
    // @Transactional
    // void testUserCannotListUsers() {
    //     headers.setBearerAuth(userToken);
    //     ResponseEntity<User[]> response = restTemplate.exchange(
    //         baseUrl + "/users",
    //         HttpMethod.GET,
    //         new HttpEntity<>(headers),
    //         User[].class
    //     );
    //     assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    // }

    // // Test cases for Role endpoints
    // @Test
    // @Transactional
    // void testAdminCanManageRoles() {
    //     headers.setBearerAuth(adminToken);
    //     ResponseEntity<Role[]> response = restTemplate.exchange(
    //         baseUrl + "/roles",
    //         HttpMethod.GET,
    //         new HttpEntity<>(headers),
    //         Role[].class
    //     );
    //     assertEquals(HttpStatus.OK, response.getStatusCode());
    // }

    // @Test
    // @Transactional
    // void testManagerCanViewRoles() {
    //     headers.setBearerAuth(managerToken);
    //     ResponseEntity<Role[]> response = restTemplate.exchange(
    //         baseUrl + "/roles",
    //         HttpMethod.GET,
    //         new HttpEntity<>(headers),
    //         Role[].class
    //     );
    //     assertEquals(HttpStatus.OK, response.getStatusCode());
    // }

    // @Test
    // @Transactional
    // void testUserCannotAccessRoles() {
    //     headers.setBearerAuth(userToken);
    //     ResponseEntity<Role[]> response = restTemplate.exchange(
    //         baseUrl + "/roles",
    //         HttpMethod.GET,
    //         new HttpEntity<>(headers),
    //         Role[].class
    //     );
    //     assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    // }

    // // Test cases for Resource endpoints
    // @Test
    // @Transactional
    // void testAdminCanManageResources() {
    //     headers.setBearerAuth(adminToken);
    //     ResponseEntity<Resource[]> response = restTemplate.exchange(
    //         baseUrl + "/resources",
    //         HttpMethod.GET,
    //         new HttpEntity<>(headers),
    //         Resource[].class
    //     );
    //     assertEquals(HttpStatus.OK, response.getStatusCode());
    // }

    // @Test
    // @Transactional
    // void testManagerCanViewResources() {
    //     headers.setBearerAuth(managerToken);
    //     ResponseEntity<Resource[]> response = restTemplate.exchange(
    //         baseUrl + "/resources",
    //         HttpMethod.GET,
    //         new HttpEntity<>(headers),
    //         Resource[].class
    //     );
    //     assertEquals(HttpStatus.OK, response.getStatusCode());
    // }

    // @Test
    // @Transactional
    // void testUserCannotAccessResources() {
    //     headers.setBearerAuth(userToken);
    //     ResponseEntity<Resource[]> response = restTemplate.exchange(
    //         baseUrl + "/resources",
    //         HttpMethod.GET,
    //         new HttpEntity<>(headers),
    //         Resource[].class
    //     );
    //     assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    // }

    // // Test cases for Organization endpoints
    // @Test
    // @Transactional
    // void testAdminCanManageOrganizations() {
    //     headers.setBearerAuth(adminToken);
    //     ResponseEntity<Organization[]> response = restTemplate.exchange(
    //         baseUrl + "/organizations",
    //         HttpMethod.GET,
    //         new HttpEntity<>(headers),
    //         Organization[].class
    //     );
    //     assertEquals(HttpStatus.OK, response.getStatusCode());
    // }

    // @Test
    // @Transactional
    // void testManagerCanViewOrganizations() {
    //     headers.setBearerAuth(managerToken);
    //     ResponseEntity<Organization[]> response = restTemplate.exchange(
    //         baseUrl + "/organizations",
    //         HttpMethod.GET,
    //         new HttpEntity<>(headers),
    //         Organization[].class
    //     );
    //     assertEquals(HttpStatus.OK, response.getStatusCode());
    // }

    // @Test
    // @Transactional
    // void testUserCannotAccessOrganizations() {
    //     headers.setBearerAuth(userToken);
    //     ResponseEntity<Organization[]> response = restTemplate.exchange(
    //         baseUrl + "/organizations",
    //         HttpMethod.GET,
    //         new HttpEntity<>(headers),
    //         Organization[].class
    //     );
    //     assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    // }

    // // Test cases for Policy endpoints
    // @Test
    // @Transactional
    // void testAdminCanManagePolicies() {
    //     headers.setBearerAuth(adminToken);
    //     ResponseEntity<Policy[]> response = restTemplate.exchange(
    //         baseUrl + "/policies",
    //         HttpMethod.GET,
    //         new HttpEntity<>(headers),
    //         Policy[].class
    //     );
    //     assertEquals(HttpStatus.OK, response.getStatusCode());
    // }

    // @Test
    // @Transactional
    // void testManagerCanViewPolicies() {
    //     headers.setBearerAuth(managerToken);
    //     ResponseEntity<Policy[]> response = restTemplate.exchange(
    //         baseUrl + "/policies",
    //         HttpMethod.GET,
    //         new HttpEntity<>(headers),
    //         Policy[].class
    //     );
    //     assertEquals(HttpStatus.OK, response.getStatusCode());
    // }

    // @Test
    // @Transactional
    // void testUserCannotAccessPolicies() {
    //     headers.setBearerAuth(userToken);
    //     ResponseEntity<Policy[]> response = restTemplate.exchange(
    //         baseUrl + "/policies",
    //         HttpMethod.GET,
    //         new HttpEntity<>(headers),
    //         Policy[].class
    //     );
    //     assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    // }
} 