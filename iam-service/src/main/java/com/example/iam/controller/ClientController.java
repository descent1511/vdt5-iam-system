package com.example.iam.controller;

import com.example.iam.dto.ClientCreateRequest;
import com.example.iam.dto.ClientResponse;
import com.example.iam.dto.ClientUpdateRequest;
import com.example.iam.entity.Client;
import com.example.iam.mapper.ClientMapper;
import com.example.iam.security.OrganizationContextHolder;
import com.example.iam.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final ClientMapper clientMapper;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<ClientResponse>> getAllClients() {
        List<Client> clients = clientService.findAll();
        List<ClientResponse> response = clients.stream()
                .map(clientMapper::toClientResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{clientId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ClientResponse> getClient(@PathVariable String clientId) {
        Client client = clientService.findByClientId(clientId);
        return ResponseEntity.ok(clientMapper.toClientResponse(client));
    }

    @PutMapping("/{clientId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ClientResponse> updateClient(@PathVariable String clientId, @Valid @RequestBody ClientUpdateRequest request) {
        Client updatedClient = clientService.updateClient(clientId, request);
        return ResponseEntity.ok(clientMapper.toClientResponse(updatedClient));
    }

    @DeleteMapping("/{clientId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteClient(@PathVariable String clientId) {
        clientService.deleteByClientId(clientId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ClientResponse> createClient(
            @Valid @RequestBody ClientCreateRequest request) {
        Long orgId = OrganizationContextHolder.getOrganizationId();
        Client newClient = clientService.createClient(orgId, request);
        ClientResponse response = clientMapper.toClientResponse(newClient);
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
} 