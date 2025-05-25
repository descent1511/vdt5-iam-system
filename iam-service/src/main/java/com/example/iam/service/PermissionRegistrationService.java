package com.example.iam.service;

import com.example.iam.entity.Permission;
import com.example.iam.repository.PermissionRepository;
import com.example.iam.security.annotation.RequirePermission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionRegistrationService {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final PermissionRepository permissionRepository;

    @PostConstruct
    public void registerPermissions() {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            
            RequirePermission requirePermission = handlerMethod.getMethodAnnotation(RequirePermission.class);
            if (requirePermission != null) {
                String permissionName = requirePermission.value();
                String description = requirePermission.description();
                
                // Check if permission already exists
                Optional<Permission> existingPermission = permissionRepository.findByName(permissionName);
                if (existingPermission.isEmpty()) {
                    // Create new permission if not exists
                    Permission permission = Permission.builder()
                        .name(permissionName)
                        .description(description)
                        .build();
                    permissionRepository.save(permission);
                    log.info("Created new permission: {}", permissionName);
                }
            }
        }
    }
} 