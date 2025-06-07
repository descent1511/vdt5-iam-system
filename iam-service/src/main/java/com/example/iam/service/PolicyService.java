package com.example.iam.service;

import com.example.iam.dto.PolicyDTO;
import com.example.iam.entity.Policy;
import com.example.iam.entity.Resource;
import com.example.iam.entity.Role;
import com.example.iam.entity.User;
import com.example.iam.entity.ClientApplication;
import com.example.iam.exception.ResourceNotFoundException;
import com.example.iam.mapper.PolicyMapper;
import com.example.iam.repository.PolicyRepository;
import com.example.iam.repository.ResourceRepository;
import com.example.iam.repository.ClientApplicationRepository;
import com.example.iam.security.UserDetailsWithOrganization;
import com.example.iam.security.UserPrincipal;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PolicyService {

    private final PolicyRepository policyRepository;
    private final PolicyMapper policyMapper;
    private final ResourceRepository resourceRepository;
    private final UserService userService;
    private final ClientApplicationRepository clientApplicationRepository;

    public List<PolicyDTO> getAllPolicies() {
        return policyRepository.findAll().stream()
                .map(policyMapper::toDTO)
                .toList();
    }

    public PolicyDTO getPolicyById(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found with id: " + id));
        return policyMapper.toDTO(policy);
    }

    @Transactional
    public PolicyDTO createPolicy(PolicyDTO dto) {
        validatePolicy(dto);
        
        // Check if policy already exists
        Resource resource = resourceRepository.findById(dto.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + dto.getResourceId()));
                
        if (policyRepository.existsBySubjectTypeAndSubjectIdAndResourceAndAction(
                dto.getSubjectType(),
                dto.getSubjectId(),
                resource,
                dto.getAction())) {
            throw new IllegalArgumentException("Policy already exists for this subject, resource and action");
        }

        Policy policy = policyMapper.toEntity(dto);
        return policyMapper.toDTO(policyRepository.save(policy));
    }

    @Transactional
    public PolicyDTO updatePolicy(Long id, PolicyDTO dto) {
        validatePolicy(dto);
        
        Policy existingPolicy = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found with id: " + id));
                
        // The PolicyMapper will now update the existing entity, preserving the organizationId
        policyMapper.updateEntityFromDto(dto, existingPolicy);
        
        // We still need to manually set the resource since it's an association
        Resource resource = resourceRepository.findById(dto.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + dto.getResourceId()));
        existingPolicy.setResource(resource);

        Policy updatedPolicy = policyRepository.save(existingPolicy);
        return policyMapper.toDTO(updatedPolicy);
    }

    @Transactional
    public void deletePolicy(Long id) {
        if (!policyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Policy not found with id: " + id);
        }
        policyRepository.deleteById(id);
    }

    public List<PolicyDTO> getPoliciesBySubject(Policy.SubjectType subjectType, Long subjectId) {
        return policyRepository.findBySubjectTypeAndSubjectId(subjectType, subjectId).stream()
                .map(policyMapper::toDTO)
                .toList();
    }

    public List<PolicyDTO> getPoliciesByEffect(Policy.Effect effect) {
        return policyRepository.findByEffect(effect).stream()
                .map(policyMapper::toDTO)
                .toList();
    }

    /**
     * Check if the request path matches the resource path pattern
     * Supports wildcard matching for path parameters
     * Example:
     * - Resource path: /api/users/{id}
     * - Request path: /api/users/123
     * - Result: true
     */
    private boolean isPathMatch(String requestPath, String resourcePath) {
        // Convert resource path pattern to regex
        String pattern = resourcePath
            .replaceAll("\\{.*?\\}", "[^/]+") // Replace {param} with regex for any non-slash characters
            .replaceAll("/", "\\\\/");        // Escape forward slashes
        
        // Add start and end anchors
        pattern = "^" + pattern + "$";
        
        boolean matches = requestPath.matches(pattern);
        log.info("Path matching - Request: {}, Pattern: {}, Matches: {}", requestPath, pattern, matches);
        return matches;
    }

    private boolean evaluateConditions(String conditionJson) {
        try {
            log.info("Evaluating conditions: {}", conditionJson);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode conditions = mapper.readTree(conditionJson);
            
            // Handle different types of conditions
            if (conditions.isObject()) {
                boolean result = evaluateObjectCondition(conditions);
                log.info("Object condition evaluation result: {}", result);
                return result;
            } else if (conditions.isArray()) {
                boolean result = evaluateArrayCondition(conditions);
                log.info("Array condition evaluation result: {}", result);
                return result;
            }
            
            log.warn("Unknown condition format");
            return false;
        } catch (Exception e) {
            log.error("Error evaluating conditions: {}", e.getMessage(), e);
            return false;
        }
    }

    private boolean evaluateObjectCondition(JsonNode condition) {
        String type = condition.get("type").asText();
        String operator = condition.get("operator").asText();
        JsonNode value = condition.get("value");
        
        log.info("Evaluating object condition - Type: {}, Operator: {}, Value: {}", 
                type, operator, value);

        boolean result = false;
        switch (type) {
            case "time":
                result = evaluateTimeCondition(operator, value);
                break;
            case "date":
                result = evaluateDateCondition(operator, value);
                break;
            case "ip":
                result = evaluateIpCondition(operator, value);
                break;
            case "role":
                result = evaluateRoleCondition(operator, value);
                break;
            case "organization":
                result = evaluateOrganizationCondition(operator, value);
                break;
            default:
                log.warn("Unknown condition type: {}", type);
                return false;
        }
        
        log.info("Object condition evaluation result: {}", result);
        return result;
    }

    private boolean evaluateArrayCondition(JsonNode conditions) {
        // Example condition format:
        // [
        //   {
        //     "type": "time",
        //     "operator": "between",
        //     "value": {
        //       "start": "09:00",
        //       "end": "17:00"
        //     }
        //   },
        //   {
        //     "type": "ip",
        //     "operator": "in",
        //     "value": ["192.168.1.0/24", "10.0.0.0/8"]
        //   }
        // ]
        
        boolean result = true;
        for (JsonNode condition : conditions) {
            result &= evaluateObjectCondition(condition);
            if (!result) break; // Short circuit if any condition fails
        }
        return result;
    }

    private boolean evaluateTimeCondition(String operator, JsonNode value) {
        LocalTime currentTime = LocalTime.now();
        log.info("Evaluating time condition - Current time: {}, Operator: {}, Value: {}", 
                currentTime, operator, value);
        
        boolean result = false;
        try {
            switch (operator) {
                case "between":
                    LocalTime start = LocalTime.parse(value.get("start").asText());
                    LocalTime end = LocalTime.parse(value.get("end").asText());
                    result = currentTime.isAfter(start) && currentTime.isBefore(end);
                    log.info("Time between check - Start: {}, End: {}, Result: {}", start, end, result);
                    break;
                    
                case "before":
                    LocalTime before = LocalTime.parse(value.asText());
                    log.info("currentTime: {}", currentTime);
                    result = currentTime.isBefore(before);
                    log.info("Time before check - Before: {}, Result: {}", before, result);
                    break;
                    
                case "after":
                    LocalTime after = LocalTime.parse(value.asText());
                    result = currentTime.isAfter(after);
                    log.info("Time after check - After: {}, Result: {}", after, result);
                    break;
                    
                default:
                    log.warn("Unknown time operator: {}", operator);
                    return false;
            }
        } catch (Exception e) {
            log.error("Error evaluating time condition: {}", e.getMessage(), e);
            return false;
        }
        
        return result;
    }

    private boolean evaluateDateCondition(String operator, JsonNode value) {
        LocalDate currentDate = LocalDate.now();
        log.info("Evaluating date condition - Current date: {}, Operator: {}, Value: {}", 
                currentDate, operator, value);
        
        boolean result = false;
        try {
            switch (operator) {
                case "between":
                    LocalDate start = LocalDate.parse(value.get("start").asText());
                    LocalDate end = LocalDate.parse(value.get("end").asText());
                    result = currentDate.isAfter(start) && currentDate.isBefore(end);
                    log.info("Date between check - Start: {}, End: {}, Result: {}", start, end, result);
                    break;
                    
                case "before":
                    LocalDate before = LocalDate.parse(value.asText());
                    result = currentDate.isBefore(before);
                    log.info("Date before check - Before: {}, Result: {}", before, result);
                    break;
                    
                case "after":
                    LocalDate after = LocalDate.parse(value.asText());
                    result = currentDate.isAfter(after);
                    log.info("Date after check - After: {}, Result: {}", after, result);
                    break;
                    
                default:
                    log.warn("Unknown date operator: {}", operator);
                    return false;
            }
        } catch (Exception e) {
            log.error("Error evaluating date condition: {}", e.getMessage(), e);
            return false;
        }
        
        return result;
    }

    private boolean evaluateIpCondition(String operator, JsonNode value) {
        String clientIp = getCurrentClientIp();
        log.info("Evaluating IP condition - Client IP: {}, Operator: {}, Value: {}", 
                clientIp, operator, value);
        
        try {
            switch (operator) {
                case "in":
                    for (JsonNode ip : value) {
                        boolean inRange = isIpInRange(clientIp, ip.asText());
                        log.info("IP in range check - IP: {}, Range: {}, Result: {}", 
                                clientIp, ip.asText(), inRange);
                        if (inRange) {
                            return true;
                        }
                    }
                    return false;
                    
                case "not_in":
                    for (JsonNode ip : value) {
                        boolean inRange = isIpInRange(clientIp, ip.asText());
                        log.info("IP not in range check - IP: {}, Range: {}, Result: {}", 
                                clientIp, ip.asText(), inRange);
                        if (inRange) {
                            return false;
                        }
                    }
                    return true;
                    
                default:
                    log.warn("Unknown IP operator: {}", operator);
                    return false;
            }
        } catch (Exception e) {
            log.error("Error evaluating IP condition: {}", e.getMessage(), e);
            return false;
        }
    }

    private boolean evaluateRoleCondition(String operator, JsonNode value) {
        Set<String> userRoles = getCurrentUserRoles();
        log.info("Evaluating role condition - User roles: {}, Operator: {}, Value: {}", 
                userRoles, operator, value);
        
        try {
            switch (operator) {
                case "has":
                    // Handle both string and array values
                    if (value.isArray()) {
                        for (JsonNode role : value) {
                            if (userRoles.contains(role.asText())) {
                                log.info("Role has check - Role: {}, Result: true", role.asText());
                                return true;
                            }
                        }
                        log.info("Role has check - No matching roles found");
                        return false;
                    } else {
                        boolean hasRole = userRoles.contains(value.asText());
                        log.info("Role has check - Role: {}, Result: {}", value.asText(), hasRole);
                        return hasRole;
                    }
                    
                case "has_any":
                    for (JsonNode role : value) {
                        if (userRoles.contains(role.asText())) {
                            log.info("Role has_any check - Role: {}, Result: true", role.asText());
                            return true;
                        }
                    }
                    log.info("Role has_any check - No matching roles found");
                    return false;
                    
                case "has_all":
                    for (JsonNode role : value) {
                        if (!userRoles.contains(role.asText())) {
                            log.info("Role has_all check - Role: {}, Result: false", role.asText());
                            return false;
                        }
                    }
                    log.info("Role has_all check - All roles found");
                    return true;
                    
                default:
                    log.warn("Unknown role operator: {}", operator);
                    return false;
            }
        } catch (Exception e) {
            log.error("Error evaluating role condition: {}", e.getMessage(), e);
            return false;
        }
    }

    private boolean evaluateOrganizationCondition(String operator, JsonNode value) {
        Long userOrgId = getCurrentUserOrganizationId();
        log.info("Evaluating organization condition - User org ID: {}, Operator: {}, Value: {}", 
                userOrgId, operator, value);
        
        try {
            // If user has no organization, deny access for organization conditions
            if (userOrgId == null) {
                log.info("User has no organization, denying access");
                return false;
            }

            switch (operator) {
                case "is":
                    boolean isOrg = userOrgId.equals(value.asLong());
                    log.info("Organization is check - Org ID: {}, Result: {}", value.asLong(), isOrg);
                    return isOrg;
                    
                case "in":
                    for (JsonNode orgId : value) {
                        boolean orgCheck = userOrgId.equals(orgId.asLong());
                        log.info("Organization in check - Org ID: {}, Result: {}", orgId.asLong(), orgCheck);
                        if (orgCheck) {
                            return true;
                        }
                    }
                    return false;
                    
                default:
                    log.warn("Unknown organization operator: {}", operator);
                    return false;
            }
        } catch (Exception e) {
            log.error("Error evaluating organization condition: {}", e.getMessage(), e);
            return false;
        }
    }

    private boolean isIpInRange(String ip, String cidr) {
        try {
            String[] parts = cidr.split("/");
            if (parts.length != 2) {
                return false;
            }
            
            String ipAddress = parts[0];
            int prefix = Integer.parseInt(parts[1]);
            
            if (prefix < 0 || prefix > 32) {
                return false;
            }
            
            long ipLong = ipToLong(ip);
            long cidrLong = ipToLong(ipAddress);
            long mask = -1L << (32 - prefix);
            
            return (ipLong & mask) == (cidrLong & mask);
        } catch (Exception e) {
            log.error("Error checking IP range: {}", e.getMessage());
            return false;
        }
    }

    private long ipToLong(String ip) {
        String[] octets = ip.split("\\.");
        long result = 0;
        for (int i = 0; i < 4; i++) {
            result <<= 8;
            result |= Integer.parseInt(octets[i]) & 0xFF;
        }
        return result;
    }

    private String getCurrentClientIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            String forwardedFor = attributes.getRequest().getHeader("X-Forwarded-For");
            if (forwardedFor != null && !forwardedFor.isEmpty()) {
                return forwardedFor.split(",")[0].trim();
            }
            return attributes.getRequest().getRemoteAddr();
        }
        return "127.0.0.1";
    }

    private Set<String> getCurrentUserRoles() {
        Set<String> roles = new HashSet<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getName();
            log.info("Getting roles for username: {}", username);
            
            try {
                User user = userService.findByUsername(username);
                if (user != null) {
                    roles = user.getRoles().stream()
                            .map(Role::getName)
                            .collect(Collectors.toSet());
                    log.info("Retrieved roles from user: {}", roles);
                } else {
                    log.warn("User not found for username: {}", username);
                }
            } catch (Exception e) {
                log.error("Error getting roles for user {}: {}", username, e.getMessage());
            }
        } else {
            log.warn("No authentication found");
        }
        return roles;
    }
    private Long getCurrentUserOrganizationId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getName();
            log.info("Getting organization ID for username: {}", username);
            
            try {
                User user = userService.findByUsername(username);
                if (user != null && user.getOrganization() != null) {
                    Long orgId = user.getOrganization().getId();
                    log.info("Retrieved organization ID: {} for user: {}", orgId, username);
                    return orgId;
                } else {
                    log.warn("User {} has no organization", username);
                }
            } catch (Exception e) {
                log.error("Error getting organization ID for user {}: {}", username, e.getMessage());
            }
        } else {
            log.warn("No authentication found");
        }
        return null;
    }

    private void validatePolicy(PolicyDTO dto) {
        if (dto.getSubjectId() == null) {
            throw new IllegalArgumentException("Subject ID is required");
        }
        
        if (dto.getSubjectType() == null) {
            throw new IllegalArgumentException("Subject type is required");
        }
        
        // Validate subject ID based on type
        if (dto.getSubjectType() == Policy.SubjectType.CLIENT) {
            if (!StringUtils.hasText(dto.getSubjectId().toString())) {
                throw new IllegalArgumentException("Client ID is required");
            }
        } else {
            if (dto.getSubjectId() <= 0) {
                throw new IllegalArgumentException("Invalid subject ID");
            }
        }
        
        if (dto.getResourceId() == null) {
            throw new IllegalArgumentException("Resource ID is required");
        }
        
        if (!StringUtils.hasText(dto.getAction())) {
            throw new IllegalArgumentException("Action is required");
        }
        
        if (dto.getEffect() == null) {
            throw new IllegalArgumentException("Effect is required");
        }
        
        // Validate condition JSON if present
        if (StringUtils.hasText(dto.getConditionJson())) {
            try {
                // You might want to add more specific JSON validation here
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid condition JSON format");
            }
        }
    }
}
