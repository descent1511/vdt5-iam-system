package com.example.iam.security;

import com.example.iam.entity.Permission;
import com.example.iam.entity.Resource;
import com.example.iam.repository.ResourceRepository;
import com.example.iam.security.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionSecurityFilter extends OncePerRequestFilter {

    private final ResourceRepository resourceRepository;
    private final JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String token = getTokenFromRequest(request);

        if (token == null || !tokenProvider.validateToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();
        String method = request.getMethod();

        try {
            Resource.HttpMethod httpMethod = Resource.HttpMethod.valueOf(method.toUpperCase());
            resourceRepository.findByPathAndMethod(path, httpMethod).ifPresent(resource -> {
                Set<String> requiredPermissions = resource.getPermissions().stream()
                        .map(Permission::getName)
                        .collect(Collectors.toSet());

                if (requiredPermissions.isEmpty()) {
                    return; // No specific permissions required for this resource
                }

                Set<String> userPermissions = tokenProvider.getPermissionsFromJWT(token);

                if (userPermissions == null || Collections.disjoint(userPermissions, requiredPermissions)) {
                    log.warn("Access denied for resource {}. Required: {}, User has: {}",
                            resource.getName(), requiredPermissions, userPermissions);
                    throw new AccessDeniedException("Insufficient permissions");
                }
            });
        } catch (IllegalArgumentException e) {
            log.trace("No enum constant for HTTP method: {}", method);
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
} 