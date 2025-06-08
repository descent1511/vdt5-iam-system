package com.example.iam.controller;

import com.example.iam.dto.ClientRegistrationRequest;
import com.example.iam.dto.ClientResponse;
import com.example.iam.entity.Client;
import com.example.iam.mapper.ClientMapper;
import com.example.iam.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organizations/{orgId}/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final ClientMapper clientMapper;

    @PostMapping
    @PreAuthorize("hasPermission(#orgId, 'client:create')")
    public ResponseEntity<ClientResponse> createClient(
            @PathVariable Long orgId,
            @Valid @RequestBody ClientRegistrationRequest request) {
        
        Client newClient = clientService.createClient(orgId, request);
        ClientResponse response = clientMapper.toClientResponse(newClient);
        
        // Important: The response should also include the generated client_secret
        // but only this one time. We need to add it to the DTO.
        // For now, returning the basic info.
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
} 