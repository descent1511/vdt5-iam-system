package com.example.iam.config;

import com.example.iam.accesscontrol.PolicyEnforcementInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
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
                    "/login",
                    "/error",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**"
                );
    }
}