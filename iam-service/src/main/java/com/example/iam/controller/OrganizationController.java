package com.example.iam.controller;

import com.example.iam.dto.OrganizationDTO;
import com.example.iam.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<OrganizationDTO> createOrganization(@RequestBody OrganizationDTO organizationDTO) {
        return ResponseEntity.ok(organizationService.createOrganization(organizationDTO));
    }   

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<OrganizationDTO> getOrganization(@PathVariable Long id) {
        return ResponseEntity.ok(organizationService.getOrganization(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<OrganizationDTO> updateOrganization(@PathVariable Long id, @RequestBody OrganizationDTO organizationDTO) {
        return ResponseEntity.ok(organizationService.updateOrganization(id, organizationDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<Void> deleteOrganization(@PathVariable Long id) {
        organizationService.deleteOrganization(id);
        return ResponseEntity.noContent().build();
    }
    

}
