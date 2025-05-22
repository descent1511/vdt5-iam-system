package com.example.iam.accesscontrol;

import com.example.iam.entity.Resource;
import org.springframework.stereotype.Component;

@Component
public class ResourceMatcher {

    public boolean matches(Resource resource, String requestPath, String method) {
        return resource.getPath().equalsIgnoreCase(requestPath)
                && resource.getMethod().name().equalsIgnoreCase(method);
    }
}
