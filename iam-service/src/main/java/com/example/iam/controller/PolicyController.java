package com.example.iam.controller;

import com.example.iam.dto.PolicyDTO;
import com.example.iam.service.PolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.iam.security.annotation.RequirePermission;
import java.util.List;

@RestController
@RequestMapping("/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;

    @GetMapping
    @RequirePermission(value = "POLICY_READ", description = "View list of policies")
    public ResponseEntity<List<PolicyDTO>> getAll() {
        return ResponseEntity.ok(policyService.getAllPolicies());
    }

    @GetMapping("/{id}")
    @RequirePermission(value = "POLICY_READ", description = "View policy details")
    public ResponseEntity<PolicyDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(policyService.getPolicyById(id));
    }

    @PostMapping
    @RequirePermission(value = "POLICY_CREATE", description = "Create new policy")
    public ResponseEntity<PolicyDTO> create(@RequestBody PolicyDTO dto) {
        return ResponseEntity.ok(policyService.createPolicy(dto));
    }

    @PutMapping("/{id}")
    @RequirePermission(value = "POLICY_UPDATE", description = "Update policy information")
    public ResponseEntity<PolicyDTO> update(@PathVariable Long id, @RequestBody PolicyDTO dto) {
        return ResponseEntity.ok(policyService.updatePolicy(id, dto));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(value = "POLICY_DELETE", description = "Delete policy")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        policyService.deletePolicy(id);
        return ResponseEntity.noContent().build();
    }
}
