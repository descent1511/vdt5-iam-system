package com.example.iam.controller;

import com.example.iam.dto.LoginRequest;
import com.example.iam.dto.SignupRequest;
import com.example.iam.dto.TokenResponse;
import com.example.iam.entity.Organization;
import com.example.iam.entity.User;
import com.example.iam.repository.OrganizationRepository;
import com.example.iam.security.JwtTokenProvider;
import com.example.iam.security.OrganizationContextFilter;
import com.example.iam.service.AuthService;
import com.example.iam.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private AuthService authService;

    @Mock
    private TokenService tokenService;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private AuthController authController;

    private LoginRequest loginRequest;
    private SignupRequest signupRequest;
    private User user;
    private TokenResponse tokenResponse;
    private Organization organization;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authController, "superAdminUsername", "superadmin");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        signupRequest = new SignupRequest();
        signupRequest.setUsername("testuser");
        signupRequest.setPassword("password123");
        signupRequest.setEmail("test@example.com");
        signupRequest.setFullName("Test User");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFullName("Test User");

        tokenResponse = new TokenResponse("test.jwt.token", "test.refresh.token");

        organization = new Organization();
        organization.setId(1L);
        organization.setActive(true);
    }

    @Test
    void login_ShouldReturnTokenResponse_WhenValidOrgId() {
        // Arrange
        when(httpServletRequest.getHeader("X-Organization-Id")).thenReturn("1");
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(organization));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateAccessToken(any(Authentication.class), eq(1L))).thenReturn("test.jwt.token");
        when(tokenProvider.generateRefreshToken(any(Authentication.class), eq(1L))).thenReturn("test.refresh.token");

        // Act
        ResponseEntity<TokenResponse> response = authController.login(loginRequest, httpServletRequest);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(tokenResponse, response.getBody());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProvider).generateAccessToken(any(Authentication.class), eq(1L));
        verify(tokenProvider).generateRefreshToken(any(Authentication.class), eq(1L));
        verify(organizationRepository).findById(1L);
    }

    @Test
    void login_ShouldReturnTokenResponse_WhenSuperAdmin() {
        // Arrange
        loginRequest.setUsername("superadmin");
        when(httpServletRequest.getHeader("X-Organization-Id")).thenReturn(null);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateAccessToken(any(Authentication.class), isNull())).thenReturn("test.jwt.token");
        when(tokenProvider.generateRefreshToken(any(Authentication.class), isNull())).thenReturn("test.refresh.token");

        // Act
        ResponseEntity<TokenResponse> response = authController.login(loginRequest, httpServletRequest);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(tokenResponse, response.getBody());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProvider).generateAccessToken(any(Authentication.class), isNull());
        verify(tokenProvider).generateRefreshToken(any(Authentication.class), isNull());
        verify(organizationRepository, never()).findById(anyLong());
    }

    @Test
    void login_ShouldThrowIllegalArgumentException_WhenOrgIdMissingForNonSuperAdmin() {
        // Arrange
        when(httpServletRequest.getHeader("X-Organization-Id")).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authController.login(loginRequest, httpServletRequest);
        });
        assertEquals("Organization ID is required for non-superadmin users.", exception.getMessage());
    }

    @Test
    void login_ShouldThrowIllegalArgumentException_WhenOrgIdInvalid() {
        // Arrange
        when(httpServletRequest.getHeader("X-Organization-Id")).thenReturn("invalid");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authController.login(loginRequest, httpServletRequest);
        });
        assertEquals("Invalid X-Organization-Id header format.", exception.getMessage());
    }

    @Test
    void login_ShouldThrowIllegalArgumentException_WhenOrganizationNotFound() {
        // Arrange
        when(httpServletRequest.getHeader("X-Organization-Id")).thenReturn("1");
        when(organizationRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authController.login(loginRequest, httpServletRequest);
        });
        assertEquals("Organization not found", exception.getMessage());
    }

    @Test
    void login_ShouldThrowIllegalArgumentException_WhenOrganizationNotActive() {
        // Arrange
        organization.setActive(false);
        when(httpServletRequest.getHeader("X-Organization-Id")).thenReturn("1");
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(organization));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authController.login(loginRequest, httpServletRequest);
        });
        assertEquals("Organization is not active", exception.getMessage());
    }

    @Test
    void signup_ShouldReturnCreatedUser_WhenValidOrgId() {
        // Arrange
        when(httpServletRequest.getAttribute(OrganizationContextFilter.ORGANIZATION_ID_ATTRIBUTE)).thenReturn("1");
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(organization));
        when(authService.existsByUsernameAndOrganizationId(anyString(), eq(1L))).thenReturn(false);
        when(authService.registerUser(any(SignupRequest.class))).thenReturn(user);

        // Act
        ResponseEntity<?> response = authController.signup(signupRequest, httpServletRequest);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(user, response.getBody());
        verify(authService).existsByUsernameAndOrganizationId("testuser", 1L);
        verify(authService).registerUser(any(SignupRequest.class));
        verify(organizationRepository).findById(1L);
    }

    @Test
    void signup_ShouldThrowIllegalArgumentException_WhenOrgIdMissing() {
        // Arrange
        when(httpServletRequest.getAttribute(OrganizationContextFilter.ORGANIZATION_ID_ATTRIBUTE)).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authController.signup(signupRequest, httpServletRequest);
        });
        assertEquals("Organization ID not found in request", exception.getMessage());
    }

    @Test
    void signup_ShouldThrowIllegalArgumentException_WhenOrganizationNotFound() {
        // Arrange
        when(httpServletRequest.getAttribute(OrganizationContextFilter.ORGANIZATION_ID_ATTRIBUTE)).thenReturn("1");
        when(organizationRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authController.signup(signupRequest, httpServletRequest);
        });
        assertEquals("Organization not found", exception.getMessage());
    }

    @Test
    void signup_ShouldThrowIllegalArgumentException_WhenOrganizationNotActive() {
        // Arrange
        organization.setActive(false);
        when(httpServletRequest.getAttribute(OrganizationContextFilter.ORGANIZATION_ID_ATTRIBUTE)).thenReturn("1");
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(organization));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authController.signup(signupRequest, httpServletRequest);
        });
        assertEquals("Organization is not active", exception.getMessage());
    }

    @Test
    void signup_ShouldThrowIllegalArgumentException_WhenUsernameExists() {
        // Arrange
        when(httpServletRequest.getAttribute(OrganizationContextFilter.ORGANIZATION_ID_ATTRIBUTE)).thenReturn("1");
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(organization));
        when(authService.existsByUsernameAndOrganizationId("testuser", 1L)).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authController.signup(signupRequest, httpServletRequest);
        });
        assertEquals("Username already exists in this organization", exception.getMessage());
    }

    @Test
    void logout_ShouldRevokeToken() {
        // Arrange
        String token = "Bearer test.jwt.token";
        when(tokenProvider.getOrganizationIdFromJWT("test.jwt.token")).thenReturn(1L);
        when(tokenProvider.getSubjectFromJWT("test.jwt.token")).thenReturn("testuser");

        // Act
        ResponseEntity<Void> response = authController.logout(token);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(tokenService).revokeToken("test.jwt.token");
        verify(tokenProvider).getOrganizationIdFromJWT("test.jwt.token");
        verify(tokenProvider).getSubjectFromJWT("test.jwt.token");
    }

    @Test
    void refreshToken_ShouldReturnNewTokens() {
        // Arrange
        String refreshToken = "Bearer test.refresh.token";
        TokenResponse newTokens = new TokenResponse("new.access.token", "new.refresh.token");
        when(authService.refreshToken("test.refresh.token")).thenReturn(newTokens);

        // Act
        ResponseEntity<TokenResponse> response = authController.refreshToken(refreshToken);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(newTokens, response.getBody());
        verify(authService).refreshToken("test.refresh.token");
    }
}