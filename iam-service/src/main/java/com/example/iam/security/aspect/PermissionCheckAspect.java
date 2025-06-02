package com.example.iam.security.aspect;

import com.example.iam.entity.Policy;
import com.example.iam.security.annotation.RequirePermission;
import com.example.iam.service.PolicyService;
import com.example.iam.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PermissionCheckAspect {

    private final PolicyService policyService;
    private final UserService userService;

    @Around("@annotation(com.example.iam.security.annotation.RequirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        // Get the annotation from the method
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RequirePermission requirePermission = signature.getMethod().getAnnotation(RequirePermission.class);
        String requiredPermission = requirePermission.value();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User is not authenticated");
        }

        // Get user permissions
        Set<String> permissions = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        // Check if user has the required permission
        if (!permissions.contains(requiredPermission)) {
            throw new AccessDeniedException("User does not have required permission: " + requiredPermission);
        }

        // Get request information
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            log.warn("No request attributes found");
            return joinPoint.proceed();
        }

        String path = attributes.getRequest().getRequestURI();
        String method = attributes.getRequest().getMethod();
        log.info(path);
        log.info( method);
        // Get username from principal
        String username;
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            username = (String) principal;
        } else {
            log.warn("Unexpected principal type: {}", principal.getClass().getName());
            return joinPoint.proceed();
        }

        // Check policy-based permission using path and method
        boolean hasPermission = policyService.checkPermissionByPath(
                Policy.SubjectType.USER,
                username,
                path,
                method,
                requiredPermission
        );

        if (!hasPermission) {
            throw new AccessDeniedException("Access denied by policy for permission: " + requiredPermission);
        }

        return joinPoint.proceed();
    }
}