package com.example.iam.audit;

import com.example.iam.entity.AuditLog;
import com.example.iam.repository.AuditLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @Pointcut("@annotation(com.example.iam.audit.Auditable)")
    public void auditablePointcut() {
    }

    @AfterReturning(pointcut = "auditablePointcut()", returning = "result")
    public void logAudit(JoinPoint joinPoint, Object result) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Auditable auditable = method.getAnnotation(Auditable.class);

            String principal = getPrincipal();
            String action = auditable.action();
            String entityType = getEntityType(result);
            String entityId = getEntityId(result);
            String details = buildDetails(joinPoint.getArgs(), result);

            AuditLog auditLog = new AuditLog(principal, action, entityType, entityId, details);
            auditLogRepository.save(auditLog);
            log.info("Audit log saved: {}", auditLog);

        } catch (Exception e) {
            log.error("Failed to save audit log", e);
        }
    }

    private String getPrincipal() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            return (String) principal;
        }
        return "anonymous";
    }

    private String getEntityType(Object result) {
        if (result == null) {
            return "N/A";
        }
        return result.getClass().getSimpleName();
    }

    private String getEntityId(Object result) {
        if (result == null) {
            return "N/A";
        }
        try {
            Method getId = result.getClass().getMethod("getId");
            Object id = getId.invoke(result);
            return id != null ? id.toString() : "N/A";
        } catch (Exception e) {
            try {
                Method getClientId = result.getClass().getMethod("getClientId");
                Object id = getClientId.invoke(result);
                return id != null ? id.toString() : "N/A";
            } catch (Exception ex) {
                log.warn("Could not determine entity ID for class {}", result.getClass().getSimpleName());
                return "N/A";
            }
        }
    }
    
    private String buildDetails(Object[] args, Object result) {
        Map<String, Object> details = new HashMap<>();
        if (args != null && args.length > 0) {
            details.put("request", args);
        }
        if (result != null) {
            details.put("response", result);
        }
        try {
            return objectMapper.writeValueAsString(details);
        } catch (JsonProcessingException e) {
            log.error("Error serializing audit details to JSON", e);
            return "{\"error\":\"Could not serialize details\"}";
        }
    }
} 