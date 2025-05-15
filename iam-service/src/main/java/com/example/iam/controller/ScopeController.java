package com.example.iam.controller;

import com.example.iam.entity.Scope;
import com.example.iam.service.ScopeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scopes")
@RequiredArgsConstructor
public class ScopeController {

    private final ScopeService scopeService;

    @GetMapping
    public ResponseEntity<List<Scope>> getAllScopes() {
        return ResponseEntity.ok(scopeService.getAllScopes());
    }

    @PostMapping
    public ResponseEntity<Scope> createScope(@RequestBody Scope scope) {
        return ResponseEntity.ok(scopeService.createScope(scope));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Scope> getScope(@PathVariable Long id) {
        return ResponseEntity.ok(scopeService.getScope(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Scope> updateScope(@PathVariable Long id, @RequestBody Scope scope) {
        return ResponseEntity.ok(scopeService.updateScope(id, scope));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScope(@PathVariable Long id) {
        scopeService.deleteScope(id);
        return ResponseEntity.ok().build();
    }

} 