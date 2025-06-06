package com.example.iam.security;

public class OrganizationContextHolder {
    private static final ThreadLocal<Long> organizationIdHolder = new ThreadLocal<>();

    public static void setOrganizationId(Long organizationId) {
        organizationIdHolder.set(organizationId);
    }

    public static Long getOrganizationId() {
        return organizationIdHolder.get();
    }

    public static void clear() {
        organizationIdHolder.remove();
    }
} 