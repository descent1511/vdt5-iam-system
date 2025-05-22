package com.example.iam.service;

import com.example.iam.entity.Resource;
import com.example.iam.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceDiscoveryService {

    private final ApplicationContext applicationContext;
    private final ResourceRepository resourceRepository;

    @Transactional
    public void discoverAndRegisterResources() {
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = mapping.getHandlerMethods();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            RequestMappingInfo mappingInfo = entry.getKey();
            HandlerMethod handlerMethod = entry.getValue();

            // Skip if the method is not annotated with @RequestMapping or its variants
            if (!handlerMethod.hasMethodAnnotation(RequestMapping.class) &&
                !handlerMethod.hasMethodAnnotation(GetMapping.class) &&
                !handlerMethod.hasMethodAnnotation(PostMapping.class) &&
                !handlerMethod.hasMethodAnnotation(PutMapping.class) &&
                !handlerMethod.hasMethodAnnotation(DeleteMapping.class) &&
                !handlerMethod.hasMethodAnnotation(PatchMapping.class)) {
                continue;
            }

            Set<String> patterns = mappingInfo.getPatternValues();
            Set<RequestMethod> methods = mappingInfo.getMethodsCondition().getMethods();

            for (String pattern : patterns) {
                for (RequestMethod method : methods) {
                    String resourceName = method.name() + " " + pattern;
                    String description = generateDescription(handlerMethod, method, pattern);

                    if (!resourceRepository.existsByName(resourceName)) {
                        Resource resource = new Resource();
                        resource.setName(resourceName);
                        resource.setDescription(description);
                        resource.setMethod(Resource.HttpMethod.valueOf(method.name()));
                        resource.setPath(pattern);
                        resourceRepository.save(resource);
                        log.info("Registered new resource: {} - {}", resourceName, description);
                    }
                }
            }
        }
    }

    private String generateDescription(HandlerMethod handlerMethod, RequestMethod method, String pattern) {
        String controllerName = handlerMethod.getBeanType().getSimpleName();
        String methodName = handlerMethod.getMethod().getName();
        return String.format("%s %s - %s.%s", method.name(), pattern, controllerName, methodName);
    }
} 