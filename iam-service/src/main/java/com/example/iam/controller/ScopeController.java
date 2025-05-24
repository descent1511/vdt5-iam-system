package com.example.iam.controller;

import com.example.iam.dto.ScopeDTO;
import com.example.iam.entity.Scope;
import com.example.iam.mapper.ScopeMapper;
import com.example.iam.service.ScopeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.iam.security.annotation.RequirePermission;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/scopes")
@RequiredArgsConstructor
public class ScopeController {

    private final ScopeService scopeService;
    private final ScopeMapper scopeMapper;

    @GetMapping
    @RequirePermission
    public ResponseEntity<List<ScopeDTO>> getAllScopes() {
        List<Scope> scopes = scopeService.getAllScopes();
        List<ScopeDTO> dtos = scopes.stream()
                .map(scopeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    @RequirePermission
    public ResponseEntity<ScopeDTO> createScope(@RequestBody ScopeDTO dto) {
        Scope scope = scopeMapper.toEntity(dto);
        Scope saved = scopeService.createScope(scope);
        return ResponseEntity.ok(scopeMapper.toDTO(saved));
    }

    @GetMapping("/{id}")
    @RequirePermission
    public ResponseEntity<ScopeDTO> getScope(@PathVariable Long id) {
        Scope scope = scopeService.getScope(id);
        return ResponseEntity.ok(scopeMapper.toDTO(scope));
    }

    @PutMapping("/{id}")
    @RequirePermission
    public ResponseEntity<ScopeDTO> updateScope(@PathVariable Long id, @RequestBody ScopeDTO dto) {
        Scope updated = scopeService.updateScope(id, scopeMapper.toEntity(dto));
        return ResponseEntity.ok(scopeMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    @RequirePermission
    public ResponseEntity<Void> deleteScope(@PathVariable Long id) {
        scopeService.deleteScope(id);
        return ResponseEntity.noContent().build();
    }
}
