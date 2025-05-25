package com.example.iam.security.aspect;

import com.example.iam.security.annotation.RequirePermission;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Aspect
@Component
@RequiredArgsConstructor
public class PermissionCheckAspect {

    @Around("@annotation(com.example.iam.security.annotation.RequirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        // Get the annotation from the method
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RequirePermission requirePermission = signature.getMethod().getAnnotation(RequirePermission.class);
        String requiredPermission = requirePermission.value();

        // Get user permissions
        Set<String> Permissions = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());

        // Check if user has the required permission
        if (!Permissions.contains(requiredPermission)) {
            throw new AccessDeniedException("User does not have required permission: " + requiredPermission);
        }

        return joinPoint.proceed();
    }
}