package com.example.iam.service;

import com.example.iam.security.OrganizationContextHolder;
import com.example.iam.dto.ServiceRegistryDto;
import com.example.iam.entity.Organization;
import com.example.iam.entity.Permission;
import com.example.iam.entity.Resource;
import com.example.iam.entity.ServiceRegistry;
import com.example.iam.mapper.ServiceRegistryMapper;
import com.example.iam.repository.OrganizationRepository;
import com.example.iam.repository.PermissionRepository;
import com.example.iam.repository.ResourceRepository;
import com.example.iam.repository.ServiceRegistryRepository;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceRegistryService {

    private final ServiceRegistryRepository serviceRegistryRepository;
    private final ResourceRepository resourceRepository;
    private final PermissionRepository permissionRepository;
    private final OrganizationRepository organizationRepository;
    private final ServiceRegistryMapper mapper = ServiceRegistryMapper.INSTANCE;

    public List<ServiceRegistryDto> getServices() {
        Long organizationId = OrganizationContextHolder.getOrganizationId();
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new IllegalStateException("Organization not found in context"));
        return serviceRegistryRepository.findByOrganization(organization).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public ServiceRegistryDto getServiceById(Long id) {
        Long organizationId = OrganizationContextHolder.getOrganizationId();
        ServiceRegistry serviceRegistry = serviceRegistryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service with id " + id + " not found."));

        if (!serviceRegistry.getOrganization().getId().equals(organizationId)) {
            throw new SecurityException("Service does not belong to the current organization.");
        }

        return mapper.toDto(serviceRegistry);
    }

    @Transactional
    public ServiceRegistryDto createService(ServiceRegistryDto serviceRegistryDto) {
        // Check if a service with the same name already exists for the organization
        Long organizationId = OrganizationContextHolder.getOrganizationId();
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new IllegalStateException("Organization not found in context"));

        serviceRegistryRepository.findByNameAndOrganization(serviceRegistryDto.getName(), organization).ifPresent(s -> {
            throw new IllegalArgumentException("Service with name '" + serviceRegistryDto.getName() + "' already exists in this organization.");
        });

        // Map DTO to entity and set organization
        ServiceRegistry serviceRegistry = mapper.toEntity(serviceRegistryDto);
        serviceRegistry.setOrganization(organization);

        // Parse OpenAPI specification and create resources
        List<Resource> resources = parseOpenApi(serviceRegistry.getUrl(), serviceRegistry);
        serviceRegistry.setResources(resources);

        // Save the service and its resources
        ServiceRegistry savedServiceRegistry = serviceRegistryRepository.save(serviceRegistry);

        return mapper.toDto(savedServiceRegistry);
    }

    @Transactional
    public ServiceRegistryDto updateService(Long id, ServiceRegistryDto serviceRegistryDto) {
        Long organizationId = OrganizationContextHolder.getOrganizationId();
        ServiceRegistry existingService = serviceRegistryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service with id " + id + " not found."));

        if (!existingService.getOrganization().getId().equals(organizationId)) {
            throw new SecurityException("Service does not belong to the current organization.");
        }

        // Update fields
        existingService.setName(serviceRegistryDto.getName());
        existingService.setDescription(serviceRegistryDto.getDescription());
        existingService.setUrl(serviceRegistryDto.getUrl());

        // For simplicity, we are not re-parsing the OpenAPI spec on update.
        // If the URL changes, a re-discovery process might be needed.

        ServiceRegistry updatedService = serviceRegistryRepository.save(existingService);
        return mapper.toDto(updatedService);
    }

    @Transactional
    public void deleteService(Long id) {
        Long organizationId = OrganizationContextHolder.getOrganizationId();
        ServiceRegistry serviceRegistry = serviceRegistryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service with id " + id + " not found."));

        if (!serviceRegistry.getOrganization().getId().equals(organizationId)) {
            throw new SecurityException("Service does not belong to the current organization.");
        }

        serviceRegistryRepository.delete(serviceRegistry);
    }

    @Transactional
    public void reloadAllResources() {
        log.info("Starting resource reloading for all services.");
        List<ServiceRegistry> services = serviceRegistryRepository.findAll();
        int totalReloaded = 0;
        int failedServices = 0;

        for (ServiceRegistry service : services) {
            log.info("Reloading resources for service: '{}' (ID: {})", service.getName(), service.getId());
            try {
                List<Resource> newResources = parseOpenApi(service.getUrl(), service);
                service.getResources().clear();
                service.getResources().addAll(newResources);
                serviceRegistryRepository.save(service);
                log.info("Successfully reloaded {} resources for service '{}'", newResources.size(), service.getName());
                totalReloaded += newResources.size();
            } catch (Exception e) {
                log.error("Failed to reload resources for service '{}' from URL '{}'. Error: {}", service.getName(), service.getUrl(), e.getMessage());
                failedServices++;
            }
        }
        log.info("Finished resource reloading. Reloaded {} resources in total. Failed services: {}.", totalReloaded, failedServices);
    }

    private List<Resource> parseOpenApi(String url, ServiceRegistry serviceRegistry) {
        String specUrl = url.endsWith("/") ? url + "api-docs" : url + "/api-docs";
        log.info("Attempting to parse OpenAPI specification from URL: {}", specUrl);
        OpenAPI openAPI = new OpenAPIV3Parser().read(specUrl);
        if (openAPI == null) {
            log.warn("Cannot read or parse OpenAPI specification from URL: {}", specUrl);
            throw new IllegalArgumentException("Cannot read OpenAPI specification from URL: " + specUrl);
        }

        List<Resource> resources = new ArrayList<>();
        for (Map.Entry<String, PathItem> entry : openAPI.getPaths().entrySet()) {
            String path = entry.getKey();
            PathItem pathItem = entry.getValue();

            pathItem.readOperationsMap().forEach((httpMethod, operation) -> {
                Resource resource = Resource.builder()
                        .name(operation.getOperationId() != null ? operation.getOperationId() : path + " " + httpMethod)
                        .description(operation.getSummary() != null ? operation.getSummary() : operation.getDescription())
                        .path(path)
                        .method(Resource.HttpMethod.valueOf(httpMethod.toString().toUpperCase()))
                        .serviceRegistry(serviceRegistry)
                        .organization(serviceRegistry.getOrganization())
                        .build();

                if (operation.getExtensions() != null) {
                    Object permissionObject = operation.getExtensions().get("x-permission");
                    if (permissionObject instanceof String) {
                        String permissionName = (String) permissionObject;
                        if (!permissionName.isBlank()) {
                            Permission permission = permissionRepository.findByName(permissionName)
                                    .orElseGet(() -> {
                                        log.info("Creating new permission '{}' from service discovery.", permissionName);
                                        return permissionRepository.save(new Permission(permissionName));
                                    });
                            resource.getPermissions().add(permission);
                        }
                    }
                }

                resources.add(resource);
            });
        }
        return resources;
    }
} 