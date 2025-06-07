package com.example.iam.controller;

import com.example.iam.dto.ScopeDTO;
import com.example.iam.entity.Scope;
import com.example.iam.mapper.ScopeMapper;
import com.example.iam.service.ScopeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;    
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/scopes")
@RequiredArgsConstructor
public class ScopeController {

    private final ScopeService scopeService;
    private final ScopeMapper scopeMapper;

    @GetMapping
    public ResponseEntity<List<ScopeDTO>> getAllScopes() {
        List<Scope> scopes = scopeService.getAllScopes();
        List<ScopeDTO> dtos = scopes.stream()
                .map(scopeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ScopeDTO> createScope(@RequestBody ScopeDTO dto) {
        Scope saved = scopeService.createScope(dto);
        return ResponseEntity.ok(scopeMapper.toDTO(saved));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ScopeDTO> getScope(@PathVariable Long id) {
        Scope scope = scopeService.getScope(id);
        return ResponseEntity.ok(scopeMapper.toDTO(scope));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ScopeDTO> updateScope(@PathVariable Long id, @RequestBody ScopeDTO dto) {
        Scope updated = scopeService.updateScope(id, dto);
        return ResponseEntity.ok(scopeMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<Void> deleteScope(@PathVariable Long id) {
        scopeService.deleteScope(id);
        return ResponseEntity.noContent().build();
    }
}
