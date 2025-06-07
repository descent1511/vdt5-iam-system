// package com.example.iam.service;

// import com.example.iam.entity.Resource;
// import com.example.iam.repository.ResourceRepository;
// import com.example.iam.security.annotation.RequirePermission;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.context.ApplicationContext;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.method.HandlerMethod;
// import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
// import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

// import java.util.Map;
// import java.util.Set;
// import java.util.concurrent.atomic.AtomicInteger;

// @Slf4j
// @Service
// @RequiredArgsConstructor
// public class ResourceDiscoveryService {

//     private final ApplicationContext applicationContext;
//     private final ResourceRepository resourceRepository;

//     @Transactional
//     public void discoverAndRegisterResources() {
//         log.info("Starting resource discovery...");
//         RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
//         Map<RequestMappingInfo, HandlerMethod> handlerMethods = mapping.getHandlerMethods();

//         AtomicInteger newResources = new AtomicInteger(0);
//         AtomicInteger updatedResources = new AtomicInteger(0);

//         log.info("Found {} handler methods", handlerMethods.size());

//         for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
//             RequestMappingInfo mappingInfo = entry.getKey();
//             HandlerMethod handlerMethod = entry.getValue();

//             // Get all patterns including class-level and method-level mappings
//             Set<String> patterns = mappingInfo.getPatternValues();
//             Set<RequestMethod> methods = mappingInfo.getMethodsCondition().getMethods();

//             // If no specific methods are defined, default to GET
//             if (methods.isEmpty()) {
//                 methods = Set.of(RequestMethod.GET);
//             }

//             log.debug("Processing handler: {} - Patterns: {} - Methods: {}", 
//                 handlerMethod.getMethod().getName(), patterns, methods);

//             for (String pattern : patterns) {
//                 for (RequestMethod method : methods) {
//                     String resourceName = method.name() + " " + pattern;
//                     String description = generateDescription(handlerMethod, method, pattern);

//                     try {
//                         resourceRepository.findByName(resourceName).ifPresentOrElse(
//                             existingResource -> {
//                                 // Update existing resource if description has changed
//                                 if (!existingResource.getDescription().equals(description)) {
//                                     existingResource.setDescription(description);
//                                     resourceRepository.save(existingResource);
//                                     updatedResources.incrementAndGet();
//                                     log.info("Updated resource: {} - {}", resourceName, description);
//                                 }
//                             },
//                             () -> {
//                                 // Create new resource
//                         Resource resource = new Resource();
//                         resource.setName(resourceName);
//                         resource.setDescription(description);
//                         resource.setMethod(Resource.HttpMethod.valueOf(method.name()));
//                         resource.setPath(pattern);
//                         resourceRepository.save(resource);
//                                 newResources.incrementAndGet();
//                         log.info("Registered new resource: {} - {}", resourceName, description);
//                             }
//                         );
//                     } catch (Exception e) {
//                         log.error("Error processing resource {}: {}", resourceName, e.getMessage(), e);
//                     }
//                 }
//             }
//         }

//         log.info("Resource discovery completed. New resources: {}, Updated resources: {}", 
//             newResources.get(), updatedResources.get());
//     }

//     private String generateDescription(HandlerMethod handlerMethod, RequestMethod method, String pattern) {
//         String controllerName = handlerMethod.getBeanType().getSimpleName();
//         String methodName = handlerMethod.getMethod().getName();
        
//         // Get permission annotation if exists
//         String permission = "";
//         RequirePermission requirePermission = handlerMethod.getMethodAnnotation(RequirePermission.class);
//         if (requirePermission != null) {
//             permission = String.format(" [Permission: %s]", requirePermission.value());
//         }
        
//         return String.format("%s %s - %s.%s%s", 
//             method.name(), pattern, controllerName, methodName, permission);
//     }
// } 