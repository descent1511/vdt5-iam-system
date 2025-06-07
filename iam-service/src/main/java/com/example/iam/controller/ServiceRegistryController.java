package com.example.iam.controller;

import com.example.iam.dto.ServiceRegistryDto;
import com.example.iam.service.ServiceRegistryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("services")
@RequiredArgsConstructor
public class ServiceRegistryController {

    private final ServiceRegistryService serviceRegistryService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<ServiceRegistryDto>> getServices() {
        List<ServiceRegistryDto> services = serviceRegistryService.getServices();
        return ResponseEntity.ok(services);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ServiceRegistryDto> getServiceById(@PathVariable Long id) {
        ServiceRegistryDto service = serviceRegistryService.getServiceById(id);
        return ResponseEntity.ok(service);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ServiceRegistryDto> createService(@Valid @RequestBody ServiceRegistryDto serviceRegistryDto) {
        ServiceRegistryDto createdService = serviceRegistryService.createService(serviceRegistryDto);
        return ResponseEntity.ok(createdService);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ServiceRegistryDto> updateService(@PathVariable Long id, @Valid @RequestBody ServiceRegistryDto serviceRegistryDto) {
        ServiceRegistryDto updatedService = serviceRegistryService.updateService(id, serviceRegistryDto);
        return ResponseEntity.ok(updatedService);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceRegistryService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
} 