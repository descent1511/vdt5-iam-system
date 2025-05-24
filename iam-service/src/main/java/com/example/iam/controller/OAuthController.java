package com.example.iam.controller;
import com.example.iam.entity.ClientApplication;
import com.example.iam.dto.ClientCreateRequest;
import com.example.iam.dto.ClientResponse;
import com.example.iam.service.OAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.iam.security.JwtTokenProvider;
import com.example.iam.security.annotation.RequirePermission;
@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class OAuthController {
    private final JwtTokenProvider tokenProvider;
    private final OAuthService oauthService;

    @PostMapping()
    @RequirePermission
    public ResponseEntity<ClientResponse> createClient(@Valid @RequestBody ClientCreateRequest request) {
        ClientApplication response = oauthService.createClientApplication(request);
        String accessToken = tokenProvider.generateClientAccessToken(response);
        return ResponseEntity.ok(new ClientResponse(
            accessToken,
            response.getName(),
            response.getDescription()
    ));
      
    }
}
