package com.example.iam.controller;

import com.example.iam.service.AuthorizationService;
import com.example.iam.dto.AuthorizationRequest;
import com.example.iam.dto.AuthorizationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authorize")
@RequiredArgsConstructor
public class AuthorizationController {

    private final AuthorizationService authorizationService;

    @PostMapping
    public ResponseEntity<AuthorizationResponse> checkPermission(@RequestBody AuthorizationRequest request) {
        boolean hasPermission = authorizationService.checkPermission(request.getToken(), request.getPath(), request.getMethod());
        return ResponseEntity.ok(new AuthorizationResponse(hasPermission));
    }
} 