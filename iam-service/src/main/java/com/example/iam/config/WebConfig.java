package com.example.iam.config;

import com.example.iam.accesscontrol.PolicyEnforcementInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final PolicyEnforcementInterceptor policyEnforcementInterceptor;

    public WebConfig(PolicyEnforcementInterceptor policyEnforcementInterceptor) {
        this.policyEnforcementInterceptor = policyEnforcementInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(policyEnforcementInterceptor)
                .excludePathPatterns(
                    "/auth/**",
                    "/auth/refresh",
                    "/login",
                    "/error",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**"
                );
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}