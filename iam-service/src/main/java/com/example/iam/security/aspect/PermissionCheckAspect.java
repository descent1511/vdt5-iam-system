package com.example.iam.security.aspect;

import com.example.iam.entity.Policy;
import com.example.iam.entity.User;
import com.example.iam.security.ClientPrincipal;
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
        log.info("Checking permission: {}", requiredPermission);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("User is not authenticated");
            throw new AccessDeniedException("User is not authenticated");
        }

        // Get user permissions
        Set<String> permissions = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        log.info("User permissions: {}", permissions);

        // Check if user has the required permission
        if (!permissions.contains(requiredPermission)) {
            log.error("User does not have required permission: {}. Available permissions: {}", 
                requiredPermission, permissions);
            throw new AccessDeniedException("User does not have required permission: " + requiredPermission);
        }
        log.info("User has required permission: {}", requiredPermission);

        // Get request information
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            log.warn("No request attributes found");
            return joinPoint.proceed();
        }

        String path = attributes.getRequest().getRequestURI();
        String method = attributes.getRequest().getMethod();
        log.info("Checking permission for path: {} and method: {}", path, method);

        // Get subject type and ID from UserDetails
        Policy.SubjectType subjectType;
        String subjectId;

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            subjectId = userDetails.getUsername();
            // Check if principal is ClientPrincipal to determine if it's a client
            subjectType = (principal instanceof ClientPrincipal) 
                ? Policy.SubjectType.CLIENT 
                : Policy.SubjectType.USER;
            log.info("Principal is UserDetails - Type: {}, ID: {}", subjectType, subjectId);
        } else if (principal instanceof String) {
            String principalId = (String) principal;
            // Try to find if this is a client ID first
            try {
                if (userService.isClientId(principalId)) {
                    subjectType = Policy.SubjectType.CLIENT;
                    subjectId = principalId;
                    log.info("Principal is client ID: {}", subjectId);
                } else {
                    // Try to find user by username
                    try {
                        User user = userService.findByUsername(principalId);
                        subjectType = Policy.SubjectType.USER;
                        subjectId = user.getUsername();
                        log.info("Principal is user ID: {}", subjectId);
                    } catch (Exception e) {
                        log.error("Error finding user with ID: {}", principalId, e);
                        throw new AccessDeniedException("Unable to determine principal type");
                    }
                }
            } catch (Exception e) {
                log.error("Error determining principal type for ID: {}", principalId, e);
                throw new AccessDeniedException("Unable to determine principal type");
            }
        } else {
            log.warn("Unexpected principal type: {}", principal.getClass().getName());
            return joinPoint.proceed();
        }

        log.info("Checking policy for subject type: {} and subject ID: {}", subjectType, subjectId);

        // Check policy-based permission using path and method
        boolean hasPermission = policyService.checkPermissionByPath(
                subjectType,
                subjectId,
                path,
                method,
                requiredPermission
        );

        if (!hasPermission) {
            log.error("Access denied by policy for permission: {} on path: {} with method: {}", 
                requiredPermission, path, method);
            throw new AccessDeniedException("Access denied by policy for permission: " + requiredPermission);
        }

        log.info("Permission check passed for: {} on path: {} with method: {}", 
            requiredPermission, path, method);
        return joinPoint.proceed();
    }
}