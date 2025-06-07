package com.example.iam.controller;

import com.example.iam.dto.LoginRequest;
import com.example.iam.entity.*;
import com.example.iam.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthorizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    private Organization organization;
    private User admin;
    private User regularUser;
    private Role adminRole;
    private Role userManagerRole;
    private Permission viewUsersPermission;
    private Permission deleteUserPermission;
    private Resource viewUsersResource;
    private Resource deleteUserResource;

    @BeforeEach
    void setUp() {
        System.out.println("Setting up test environment at " + java.time.LocalDateTime.now());

        // Step 1: Create Organization
        organization = createAndVerifyOrganization("Test Corp", true);

        // Step 2: Create Admin User and Role
        admin = createAndVerifyUser("admin", "admin123", organization);
        adminRole = createAndVerifyRole("ROLE_ADMIN", "Administrator role", organization,
                Set.of(createAndVerifyPermission("USER_READ_ALL", "Read all users"),
                        createAndVerifyPermission("USER_DELETE", "Delete a user")));
        admin.setRoles(new HashSet<>(Set.of(adminRole)));
        userRepository.save(admin);
        verifyUserRoles(admin, Set.of("ROLE_ADMIN"));

        // Step 3: Create Regular User
        regularUser = createAndVerifyUser("testuser", "userpass", organization);

        // Step 4: Create UserManager Role
        userManagerRole = createAndVerifyRole("USER_MANAGER", "Manages users", organization,
                Set.of(createAndVerifyPermission("USER_READ_ALL", "Read all users")));
        regularUser.setRoles(new HashSet<>(Set.of(userManagerRole)));
        userRepository.save(regularUser);
        verifyUserRoles(regularUser, Set.of("USER_MANAGER"));

        // Step 5: Create Resources
        viewUsersResource = createAndVerifyResource("View Users", "/api/users", Resource.HttpMethod.GET,
                Set.of(createAndVerifyPermission("USER_READ_ALL", "Read all users")));
        deleteUserResource = createAndVerifyResource("Delete User", "/api/users/{id}", Resource.HttpMethod.DELETE,
                Set.of(createAndVerifyPermission("USER_DELETE", "Delete a user")));

        System.out.println("Test setup completed.");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void fullAuthorizationFlowTest() throws Exception {
        System.out.println("Starting fullAuthorizationFlowTest at " + java.time.LocalDateTime.now());

        // PART 1: Admin logs in and verifies user roles
        String adminToken = loginAndGetToken("admin", "admin123", organization.getId());
        System.out.println("Admin Token: " + adminToken);

        MvcResult userResult = mockMvc.perform(get("/users/" + regularUser.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .header("X-Organization-Id", organization.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode userResponse = objectMapper.readTree(userResult.getResponse().getContentAsString());
        System.out.println("User response: " + userResponse.toString());
        JsonNode roles = userResponse.get("roles");
        assertTrue(roles != null && roles.isArray() && roles.size() == 1 && roles.get(0).asText().equals("USER_MANAGER"),
                () -> "User should have USER_MANAGER role");
        assertTrue(userResponse.get("permissions").isArray() && userResponse.get("permissions").size() == 1,
                () -> "User should have one permission");

        // PART 2: User logs in and tries to authorize
        String userToken = loginAndGetToken("testuser", "userpass", organization.getId());
        System.out.println("User Token: " + userToken);

        // PART 3: Verification using /authorize endpoint
        String authorizeGetRequest = objectMapper.writeValueAsString(new AuthorizeRequest(userToken, "/api/users", "GET"));
        System.out.println("Authorize request (GET): " + authorizeGetRequest);
        MvcResult permittedResult = mockMvc.perform(post("/authorize")
                        .header("X-Organization-Id", organization.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorizeGetRequest))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode permittedResponse = objectMapper.readTree(permittedResult.getResponse().getContentAsString());
        System.out.println("Permitted response: " + permittedResponse.toString());
        assertTrue(permittedResponse.get("allowed").asBoolean(), () -> "User should be allowed to GET /api/users");

        String authorizeDeleteRequest = objectMapper.writeValueAsString(new AuthorizeRequest(userToken, "/api/users/{id}", "DELETE"));
        System.out.println("Authorize request (DELETE): " + authorizeDeleteRequest);
        MvcResult forbiddenResult = mockMvc.perform(post("/authorize")
                        .header("X-Organization-Id", organization.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorizeDeleteRequest))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode forbiddenResponse = objectMapper.readTree(forbiddenResult.getResponse().getContentAsString());
        System.out.println("Forbidden response: " + forbiddenResponse.toString());
        assertFalse(forbiddenResponse.get("allowed").asBoolean(), () -> "User should not be allowed to DELETE /api/users/" + regularUser.getId());
    }

    // Helper classes
    private static class AuthorizeRequest {
        private final String token;
        private final String path;
        private final String method;

        public AuthorizeRequest(String token, String path, String method) {
            this.token = token;
            this.path = path;
            this.method = method;
        }

        public String getToken() { return token; }
        public String getPath() { return path; }
        public String getMethod() { return method; }
    }

    // Helper methods for creation and verification
    private Organization createAndVerifyOrganization(String name, boolean active) {
        Organization org = new Organization();
        org.setName(name);
        org.setActive(active);
        Organization savedOrg = organizationRepository.save(org);
        verifyOrganization(savedOrg, name, active);
        return savedOrg;
    }

    private User createAndVerifyUser(String username, String password, Organization org) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(username + "@example.com");
        user.setFullName("Full Name for " + username);
        user.setOrganization(org);
        User savedUser = userRepository.save(user);
        verifyUser(savedUser, username, org);
        return savedUser;
    }

    private Role createAndVerifyRole(String name, String description, Organization org, Set<Permission> permissions) {
        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        role.setOrganization(org);
        role.setPermissions(new HashSet<>(permissions));
        Role savedRole = roleRepository.save(role);
        verifyRole(savedRole, name, description, org, permissions);
        return savedRole;
    }

    private Permission createAndVerifyPermission(String name, String description) {
        Permission permission = new Permission();
        permission.setName(name);
        permission.setDescription(description);
        Permission savedPermission = permissionRepository.save(permission);
        verifyPermission(savedPermission, name, description);
        return savedPermission;
    }

    private Resource createAndVerifyResource(String name, String path, Resource.HttpMethod method, Set<Permission> permissions) {
        Resource resource = new Resource();
        resource.setName(name);
        resource.setDescription(name + " resource");
        resource.setPath(path);
        resource.setMethod(method);
        resource.setPermissions(permissions);
        Resource savedResource = resourceRepository.save(resource);
        verifyResource(savedResource, name, path, method, permissions);
        return savedResource;
    }

    // Verification methods
    private void verifyOrganization(Organization org, String expectedName, boolean expectedActive) {
        Optional<Organization> fetchedOrg = organizationRepository.findById(org.getId());
        assertTrue(fetchedOrg.isPresent(), "Organization should be saved");
        assertEquals(expectedName, fetchedOrg.get().getName(), "Organization name should match");
        assertEquals(expectedActive, fetchedOrg.get().isActive(), "Organization active status should match");
    }

    private void verifyUser(User user, String expectedUsername, Organization expectedOrg) {
        Optional<User> fetchedUser = userRepository.findById(user.getId());
        assertTrue(fetchedUser.isPresent(), "User should be saved");
        assertEquals(expectedUsername, fetchedUser.get().getUsername(), "User username should match");
        assertEquals(expectedOrg.getId(), fetchedUser.get().getOrganization().getId(), "User organization should match");
    }

    private void verifyUserRoles(User user, Set<String> expectedRoleNames) {
        Optional<User> fetchedUser = userRepository.findById(user.getId());
        assertTrue(fetchedUser.isPresent(), "User should be saved");
        Set<String> actualRoleNames = fetchedUser.get().getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        assertEquals(expectedRoleNames, actualRoleNames, "User roles should match");
    }

    private void verifyRole(Role role, String expectedName, String expectedDescription, Organization expectedOrg, Set<Permission> expectedPermissions) {
        Optional<Role> fetchedRole = roleRepository.findById(role.getId());
        assertTrue(fetchedRole.isPresent(), "Role should be saved");
        assertEquals(expectedName, fetchedRole.get().getName(), "Role name should match");
        assertEquals(expectedDescription, fetchedRole.get().getDescription(), "Role description should match");
        assertEquals(expectedOrg.getId(), fetchedRole.get().getOrganization().getId(), "Role organization should match");
        assertEquals(expectedPermissions.size(), fetchedRole.get().getPermissions().size(), "Number of permissions should match");
        for (Permission perm : expectedPermissions) {
            assertTrue(fetchedRole.get().getPermissions().stream().anyMatch(p -> p.getName().equals(perm.getName())),
                    () -> "Permission " + perm.getName() + " should be linked to role");
        }
    }

    private void verifyPermission(Permission permission, String expectedName, String expectedDescription) {
        Optional<Permission> fetchedPermission = permissionRepository.findById(permission.getId());
        assertTrue(fetchedPermission.isPresent(), "Permission should be saved");
        assertEquals(expectedName, fetchedPermission.get().getName(), "Permission name should match");
        assertEquals(expectedDescription, fetchedPermission.get().getDescription(), "Permission description should match");
    }

    private void verifyResource(Resource resource, String expectedName, String expectedPath, Resource.HttpMethod expectedMethod, Set<Permission> expectedPermissions) {
        Optional<Resource> fetchedResource = resourceRepository.findById(resource.getId());
        assertTrue(fetchedResource.isPresent(), "Resource should be saved");
        assertEquals(expectedName, fetchedResource.get().getName(), "Resource name should match");
        assertEquals(expectedPath, fetchedResource.get().getPath(), "Resource path should match");
        assertEquals(expectedMethod, fetchedResource.get().getMethod(), "Resource method should match");
        assertEquals(expectedPermissions.size(), fetchedResource.get().getPermissions().size(), "Number of permissions should match");
        for (Permission perm : expectedPermissions) {
            assertTrue(fetchedResource.get().getPermissions().stream().anyMatch(p -> p.getName().equals(perm.getName())),
                    () -> "Permission " + perm.getName() + " should be linked to resource");
        }
    }

    private String loginAndGetToken(String username, String password, Long orgId) throws Exception {
        System.out.println("Attempting login for " + username + " with orgId: " + orgId);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .header("X-Organization-Id", orgId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        System.out.println("Login Response for " + username + ": " + responseString);
        return objectMapper.readTree(responseString).get("accessToken").asText();
    }
}