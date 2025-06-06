// package com.example.iam.controller;
// import com.example.iam.entity.ClientApplication;
// import com.example.iam.dto.ClientCreateRequest;
// import com.example.iam.dto.ClientUpdateRequest;
// import com.example.iam.dto.ClientResponse;
// import com.example.iam.service.OAuthService;
// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;
// import com.example.iam.security.JwtTokenProvider;
// import com.example.iam.security.annotation.RequirePermission;
// import java.util.List;

// @RestController
// @RequestMapping("/clients")
// @RequiredArgsConstructor
// public class OAuthController {
//     private final JwtTokenProvider tokenProvider;
//     private final OAuthService oauthService;

//     @PostMapping()
//     @RequirePermission(value = "CLIENT_CREATE", description = "Create new OAuth client application")
//     public ResponseEntity<ClientResponse> createClient(@Valid @RequestBody ClientCreateRequest request) {
//         ClientApplication response = oauthService.createClientApplication(request);
//         String accessToken = tokenProvider.generateClientAccessToken(response);
//         return ResponseEntity.ok(new ClientResponse(
//             response.getId(),
//             accessToken,
//             response.getName(),
//             response.getDescription()
//         ));
//     }

//     @GetMapping
//     @RequirePermission(value = "CLIENT_READ", description = "Get all OAuth client applications")
//     public ResponseEntity<List<ClientResponse>> getAllClients() {
//         List<ClientApplication> clients = oauthService.getAllClients();
//         List<ClientResponse> responses = clients.stream()
//             .map(client -> new ClientResponse(
//                 client.getId(),
//                 tokenProvider.generateClientAccessToken(client),
//                 client.getName(),
//                 client.getDescription()
//             ))
//             .toList();
//         return ResponseEntity.ok(responses);
//     }

//     @GetMapping("/{clientId}")
//     @RequirePermission(value = "CLIENT_READ", description = "Get OAuth client application by ID")
//     public ResponseEntity<ClientResponse> getClientById(@PathVariable String clientId) {
//         ClientApplication client = oauthService.getClientById(clientId);
//         String accessToken = tokenProvider.generateClientAccessToken(client);
//         return ResponseEntity.ok(new ClientResponse(
//             client.getId(),
//             accessToken,
//             client.getName(),
//             client.getDescription()
//         ));
//     }

//     @PutMapping("/{clientId}")
//     @RequirePermission(value = "CLIENT_UPDATE", description = "Update OAuth client application")
//     public ResponseEntity<ClientResponse> updateClient(
//             @PathVariable String clientId,
//             @Valid @RequestBody ClientUpdateRequest request) {
//         ClientApplication client = oauthService.updateClient(clientId, request);
//         String accessToken = tokenProvider.generateClientAccessToken(client);
//         return ResponseEntity.ok(new ClientResponse(
//             client.getId(),
//             accessToken,
//             client.getName(),
//             client.getDescription()
//         ));
//     }

//     @DeleteMapping("/{clientId}")
//     @RequirePermission(value = "CLIENT_DELETE", description = "Delete client application")
//     public ResponseEntity<Void> deleteClient(@PathVariable Long clientId) {
//         oauthService.deleteClient(clientId);
//         return ResponseEntity.noContent().build();
//     }
// }
