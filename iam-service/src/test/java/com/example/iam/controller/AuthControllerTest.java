// package com.example.iam.controller;

// import com.example.iam.dto.LoginRequest;
// import com.example.iam.dto.SignupRequest;
// import com.example.iam.dto.TokenResponse;
// import com.example.iam.entity.User;
// import com.example.iam.security.JwtTokenProvider;
// import com.example.iam.service.AuthService;
// import com.example.iam.service.TokenService;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class)
// class AuthControllerTest {

//     @Mock
//     private AuthenticationManager authenticationManager;

//     @Mock
//     private JwtTokenProvider tokenProvider;

//     @Mock
//     private AuthService authService;

//     @Mock
//     private TokenService tokenService;

//     @Mock
//     private Authentication authentication;

//     @InjectMocks
//     private AuthController authController;

//     private LoginRequest loginRequest;
//     private SignupRequest signupRequest;
//     private User user;
//     private TokenResponse tokenResponse;

//     @BeforeEach
//     void setUp() {
//         loginRequest = new LoginRequest();
//         loginRequest.setUsername("testuser");
//         loginRequest.setPassword("password123");

//         signupRequest = new SignupRequest();
//         signupRequest.setUsername("testuser");
//         signupRequest.setPassword("password123");
//         signupRequest.setEmail("test@example.com");
//         signupRequest.setFullName("Test User");

//         user = new User();
//         user.setId(1L);
//         user.setUsername("testuser");
//         user.setEmail("test@example.com");
//         user.setFullName("Test User");

//         tokenResponse = new TokenResponse("test.jwt.token", "test.refresh.token");
//     }

//     // @Test
//     // void login_ShouldReturnTokenResponse() {
//     //     // Arrange
//     //     when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//     //         .thenReturn(authentication);
//     //     when(tokenProvider.generateAccessToken(authentication)).thenReturn("test.jwt.token");
//     //     when(tokenProvider.generateRefreshToken(authentication)).thenReturn("test.refresh.token");

//     //     // Act
//     //     ResponseEntity<TokenResponse> response = authController.login(loginRequest);

//     //     // Assert
//     //     assertNotNull(response);
//     //     assertEquals(200, response.getStatusCode().value());
//     //     assertEquals(tokenResponse, response.getBody());
//     //     verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
//     //     verify(tokenProvider).generateAccessToken(authentication);
//     //     verify(tokenProvider).generateRefreshToken(authentication);
//     // }

//     @Test
//     void signup_ShouldReturnCreatedUser() {
//         // Arrange
//         when(authService.registerUser(any(SignupRequest.class))).thenReturn(user);

//         // Act
//         ResponseEntity<?> response = authController.signup(signupRequest);

//         // Assert
//         assertNotNull(response);
//         assertEquals(200, response.getStatusCode().value());
//         assertEquals(user, response.getBody());
//         verify(authService).registerUser(any(SignupRequest.class));
//     }

//     @Test
//     void logout_ShouldRevokeToken() {
//         // Arrange
//         String token = "Bearer test.jwt.token";

//         // Act
//         ResponseEntity<Void> response = authController.logout(token);

//         // Assert
//         assertNotNull(response);
//         assertEquals(200, response.getStatusCode().value());
//         verify(tokenService).revokeToken("test.jwt.token");
//     }
// } 