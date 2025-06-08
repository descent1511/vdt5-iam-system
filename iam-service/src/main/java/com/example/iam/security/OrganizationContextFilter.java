package com.example.iam.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OrganizationContextFilter implements Filter {
    public static final String ORGANIZATION_ID_ATTRIBUTE = "organizationId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, jakarta.servlet.ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // Only process if there's no Authorization header
        if (httpRequest.getHeader("Authorization") == null) {
        String orgIdStr = httpRequest.getHeader("X-Organization-Id"); 
        System.out.println("orgIdStr: " + orgIdStr);
        if (orgIdStr != null) {
                try {
            Long organizationId = Long.parseLong(orgIdStr);
            OrganizationContextHolder.setOrganizationId(organizationId);
            httpRequest.setAttribute(ORGANIZATION_ID_ATTRIBUTE, orgIdStr);
                } catch (NumberFormatException e) {
                    // Log the error or handle it as needed
                }
            }
        }
        
            chain.doFilter(request, response);
    }
}