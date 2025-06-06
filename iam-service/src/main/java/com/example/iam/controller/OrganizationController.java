package com.example.iam.controller;

import com.example.iam.dto.OrganizationDTO;
import com.example.iam.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.iam.security.annotation.RequirePermission;
import java.util.List;

@RestController
@RequestMapping("/organizations")
@RequiredArgsConstructor
public class OrganizationController {
    private final OrganizationService organizationService;

    @GetMapping
    public ResponseEntity<List<OrganizationDTO>> getAllOrganizations() {
        return ResponseEntity.ok(organizationService.getAllOrganizations());
    }

    @PostMapping
    @RequirePermission(value = "ORGANIZATION_CREATE", description = "Create new organization")
    public ResponseEntity<OrganizationDTO> createOrganization(@RequestBody OrganizationDTO organizationDTO) {
        return ResponseEntity.ok(organizationService.createOrganization(organizationDTO));
    }   

    @GetMapping("/{id}")
    @RequirePermission(value = "ORGANIZATION_READ", description = "View organization details")
    public ResponseEntity<OrganizationDTO> getOrganization(@PathVariable Long id) {
        return ResponseEntity.ok(organizationService.getOrganization(id));
    }

    @PutMapping("/{id}")
    @RequirePermission(value = "ORGANIZATION_UPDATE", description = "Update organization information")
    public ResponseEntity<OrganizationDTO> updateOrganization(@PathVariable Long id, @RequestBody OrganizationDTO organizationDTO) {
        return ResponseEntity.ok(organizationService.updateOrganization(id, organizationDTO));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(value = "ORGANIZATION_DELETE", description = "Delete organization")
    public ResponseEntity<Void> deleteOrganization(@PathVariable Long id) {
        organizationService.deleteOrganization(id);
        return ResponseEntity.noContent().build();
    }
    

}
