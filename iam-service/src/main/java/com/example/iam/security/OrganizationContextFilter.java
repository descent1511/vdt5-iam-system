package com.example.iam.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.AuthenticationException;

@Component
@Slf4j
public class OrganizationContextFilter implements Filter {

    public static final String ORGANIZATION_ID_ATTRIBUTE = "organizationId";
    private static final String SUPER_ADMIN_ROLE = "ROLE_SUPER_ADMIN";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String organizationIdStr = httpRequest.getHeader("X-Organization-Id");

        // If header is not present, try to get it from the session (for login redirect flow)
        if (organizationIdStr == null) {
            HttpSession session = httpRequest.getSession(false);
            if (session != null) {
                organizationIdStr = (String) session.getAttribute(SessionSavingAuthenticationEntryPoint.ORG_ID_SESSION_ATTRIBUTE);
                if (organizationIdStr != null) {
                    log.debug("Found organizationId '{}' in session.", organizationIdStr);
                }
            }
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isSuperAdmin = authentication != null && 
            authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(SUPER_ADMIN_ROLE));
        
        if (organizationIdStr != null) {
            try {
                Long orgId = Long.parseLong(organizationIdStr);
                OrganizationContextHolder.setOrganizationId(orgId);
                // Also set as request attribute for other components if needed
                httpRequest.setAttribute(ORGANIZATION_ID_ATTRIBUTE, orgId);
                log.debug("Set organization context from string '{}' to long '{}'", organizationIdStr, orgId);
            } catch (NumberFormatException e) {
                log.warn("Invalid organization ID format in header or session: {}", organizationIdStr);
            }
        } else if (!isSuperAdmin) {
            log.warn("No X-Organization-Id header provided for non-superadmin user");
        }
        
        // IMPORTANT: Clear context logic is now handled by OrganizationContextClearFilter
        // We just pass the request along the chain.
        chain.doFilter(request, response);
    }
}