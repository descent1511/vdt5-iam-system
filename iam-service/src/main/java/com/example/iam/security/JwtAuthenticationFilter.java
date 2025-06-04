package com.example.iam.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.util.Set;
import java.io.IOException;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = getJwtFromRequest(request);
        logger.info("JWT from request: " + jwt);
        
        if (StringUtils.hasText(jwt)) {
            try {
                logger.info("Validating token...");
                if (tokenProvider.validateToken(jwt)) {
                    String subject = tokenProvider.getSubjectFromJWT(jwt);
                    String subjectType = tokenProvider.getSubjectTypeFromJWT(jwt);
                    logger.info("Token validated - Subject: " + subject + ", Type: " + subjectType);
                    
                    Set<String> authorities;
    
                    if ("user".equals(subjectType)) {
                        String username = tokenProvider.getUsernameFromJWT(jwt);
                        logger.info("Processing user authentication for username: " + username);
                        
                        var userDetails = userDetailsService.loadUserByUsername(username);
                        logger.info("UserDetails loaded: " + (userDetails != null ? "success" : "null"));
                        
                        authorities = userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toSet());
                        logger.info("User authorities: " + authorities);
                        
                    } else if ("client".equals(subjectType)) {
                        String client_id = tokenProvider.getClientIdFromJWT(jwt);
                        logger.info("Processing client authentication for client_id: " + client_id);
                        
                        var userDetails = userDetailsService.loadUserByUsername(client_id);
                        logger.info("Client UserDetails loaded: " + (userDetails != null ? "success" : "null"));
                        
                        authorities = userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toSet());
                        logger.info("Client authorities: " + authorities);
                    } else {
                        logger.error("Invalid subject type: " + subjectType);
                        throw new IllegalArgumentException("Invalid subject type: " + subjectType);
                    }
    
                    logger.info("Creating authentication token with subject: " + subject + " and authorities: " + authorities);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            subject,
                            jwt,
                            authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.info("Authentication set in SecurityContext");
                } else {
                    logger.warn("Token validation failed");
                }
            } catch (Exception ex) {
                logger.error("Token validation error: " + ex.getMessage(), ex);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or expired token");
                return;
            }
        } else {
            logger.debug("No JWT token found in request");
        }
    
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
} 