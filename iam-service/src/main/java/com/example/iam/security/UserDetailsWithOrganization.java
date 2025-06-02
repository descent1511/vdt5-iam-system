package com.example.iam.security;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserDetailsWithOrganization extends UserDetails {
    Long getOrganizationId();
} 