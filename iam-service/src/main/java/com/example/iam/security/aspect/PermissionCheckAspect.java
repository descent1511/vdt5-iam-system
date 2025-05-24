package com.example.iam.security.aspect;

import com.example.iam.entity.Permission;
import com.example.iam.entity.Resource;
import com.example.iam.entity.Role;
import com.example.iam.entity.User;
import com.example.iam.exception.AccessDeniedException;
import com.example.iam.repository.ResourceRepository;
import com.example.iam.security.annotation.RequirePermission;
import com.example.iam.service.UserService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Set;
import java.util.stream.Collectors;

@Aspect
@Component
@RequiredArgsConstructor
public class PermissionCheckAspect {

    private final ResourceRepository resourceRepository;

    @Around("@annotation(com.example.iam.security.annotation.RequirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String requestPath = request.getRequestURI();
        String method = request.getMethod();

        // Find resource by path and method
        Resource resource = resourceRepository.findByPathAndMethod(requestPath, Resource.HttpMethod.valueOf(method))
            .orElseThrow(() -> new AccessDeniedException("Resource not found: " + requestPath));
            
        // Get user permissions
        Set<String> Permissions = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());

        // Check if user has any of the required permissions for this resource
        boolean hasPermission = resource.getPermissions().stream()
            .map(Permission::getName)
            .anyMatch(Permissions::contains);

        if (!hasPermission) {
            throw new AccessDeniedException("User does not have required permission for resource: " + requestPath);
        }

        return joinPoint.proceed();
    }
}