package com.example.iam.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.io.IOException;

@Slf4j
public class SessionSavingAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final AuthenticationEntryPoint delegate;
    public static final String ORG_ID_SESSION_ATTRIBUTE = "organizationId";

    public SessionSavingAuthenticationEntryPoint(String loginFormUrl) {
        this.delegate = new LoginUrlAuthenticationEntryPoint(loginFormUrl);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String organizationId = request.getHeader("X-Organization-Id");
        if (organizationId != null) {
            HttpSession session = request.getSession();
            session.setAttribute(ORG_ID_SESSION_ATTRIBUTE, organizationId);
            log.debug("Saved organizationId '{}' to session.", organizationId);
        }
        delegate.commence(request, response, authException);
    }
} 