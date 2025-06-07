package com.example.iam.controller;

import com.example.iam.dto.PolicyDTO;
import com.example.iam.service.PolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
@RestController
@RequestMapping("/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;

    @GetMapping
    public ResponseEntity<List<PolicyDTO>> getAll() {
        return ResponseEntity.ok(policyService.getAllPolicies());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<PolicyDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(policyService.getPolicyById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<PolicyDTO> create(@RequestBody PolicyDTO dto) {
        return ResponseEntity.ok(policyService.createPolicy(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<PolicyDTO> update(@PathVariable Long id, @RequestBody PolicyDTO dto) {
        return ResponseEntity.ok(policyService.updatePolicy(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        policyService.deletePolicy(id);
        return ResponseEntity.noContent().build();
    }
}
