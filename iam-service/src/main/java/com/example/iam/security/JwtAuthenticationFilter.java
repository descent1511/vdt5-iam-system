package com.example.iam.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import com.example.iam.exception.InvalidTokenException;
import org.springframework.security.core.AuthenticationException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        try {
        String jwt = getJwtFromRequest(request);

        if (StringUtils.hasText(jwt)) {
                // This will throw InvalidTokenException for any validation failures.
                tokenProvider.validateToken(jwt);

                // Token is valid, set up the security context
                    Long organizationId = tokenProvider.getOrganizationIdFromJWT(jwt);
                    OrganizationContextHolder.setOrganizationId(organizationId);
                    
                String subject = tokenProvider.getSubjectFromJWT(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
    
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("Authentication set for subject: {}", subject);
            }
            filterChain.doFilter(request, response);
        } catch (AuthenticationException e) {
            log.error("Authentication error in JWT filter: {}", e.getMessage());
            // The exception is now an AuthenticationException (or subclass like InvalidTokenException)
            // It will be handled by the DelegatedAuthenticationEntryPoint
            throw e;
            } finally {
            // Always clear the organization context after the request has been handled
                OrganizationContextHolder.clear();
            }
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
} 