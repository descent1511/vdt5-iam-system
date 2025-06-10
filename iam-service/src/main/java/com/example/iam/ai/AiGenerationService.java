package com.example.iam.ai;

import com.example.iam.dto.RoleDTO;
import com.example.iam.entity.Permission;
import com.example.iam.repository.PermissionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;
import com.example.iam.dto.AiRoleResponseDTO;
import com.example.iam.dto.AiClientResponseDTO;
import com.example.iam.dto.ClientCreateRequest;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AiGenerationService {

    private final ChatClient chatClient;
    private final PermissionRepository permissionRepository;
    private final ObjectMapper objectMapper;

    public AiGenerationService(ChatClient chatClient, PermissionRepository permissionRepository, ObjectMapper objectMapper) {
        this.chatClient = chatClient;
        this.permissionRepository = permissionRepository;
        this.objectMapper = objectMapper;
    }

    public Object generateEntity(String entityType, String userDescription) {
        if ("role".equalsIgnoreCase(entityType)) {
            return generateRole(userDescription);
        } else if ("client".equalsIgnoreCase(entityType)) {
            return generateClient(userDescription);
        }
        throw new IllegalArgumentException("Unsupported entity type: " + entityType);
    }

    private AiRoleResponseDTO generateRole(String userDescription) {
        List<String> allPermissions = permissionRepository.findAll().stream()
                .map(Permission::getName)
                .collect(Collectors.toList());

        String jsonExample = getRoleJsonExample();

        String promptText = """
            You are an expert Identity and Access Management (IAM) system architect.
            Your task is to generate a JSON object for a new user role based on a natural language description.

            The JSON output MUST strictly follow this structure and field names:
            - "name": A short, descriptive name for the role (e.g., "Content Moderator").
            - "description": A brief explanation of the role's purpose.
            - "permissions": An array of strings. Each string in this array MUST be one of the "availablePermissions" listed below.

            Here is an example of a valid JSON object:
            ```json
            {jsonExample}
            ```

            Available permissions are:
            {availablePermissions}

            Now, process the following user request and generate the corresponding JSON object. Ensure the output is only the raw JSON, without any extra text or markdown formatting.
            User Request: "{userDescription}"
            """;

        PromptTemplate promptTemplate = new PromptTemplate(promptText);
        Map<String, Object> promptData = Map.of(
                "jsonExample", jsonExample,
                "availablePermissions", allPermissions.toString(),
                "userDescription", userDescription
        );
        System.out.println("Prompt data: " + promptData);
        String jsonResponse = chatClient.prompt(promptTemplate.create(promptData))
                .call()
                .content();

        // More robust markdown removal
        jsonResponse = jsonResponse.replace("```json", "");
        jsonResponse = jsonResponse.replace("```", "");
        jsonResponse = jsonResponse.trim();

        try {
            System.out.println("JSON Response: " + jsonResponse);
            return objectMapper.readValue(jsonResponse, AiRoleResponseDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse AI response to AiRoleResponseDTO", e);
        }
    }

    private String getRoleJsonExample() {
        RoleDTO example = new RoleDTO();
        example.setName("Example Role");
        example.setDescription("An example description of what this role does.");
        example.setPermissions(Set.of("PRODUCT_READ", "PRODUCT_CREATE"));
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(example);
        } catch (JsonProcessingException e) {
            return "{}"; // Fallback to empty JSON
        }
    }

    private AiClientResponseDTO generateClient(String userDescription) {
        // Example permissions (you might fetch these from a repository similar to how roles do)
        List<String> availableScopes = List.of("openid", "profile", "email", "offline_access");
        List<String> availableGrantTypes = List.of("authorization_code", "client_credentials", "refresh_token", "password");
        List<String> availableAuthMethods = List.of("client_secret_basic", "client_secret_post", "none");

        String jsonExample = getClientJsonExample();

        String promptText = """
            You are an expert Identity and Access Management (IAM) system architect.
            Your task is to generate a JSON object for a new OAuth2 client based on a natural language description.

            The JSON output MUST strictly follow this structure and field names:
            - "clientName": A unique name for the client (e.g., "WebAppClient").
            - "description": A brief explanation of the client's purpose.
            - "redirectUris": An array of strings. These are the URIs to which the authorization server will redirect the user-agent after an authorization grant has been issued.
            - "authorizationGrantTypes": An array of strings. Each string in this array MUST be one of the "availableGrantTypes" listed below.
            - "clientAuthenticationMethods": An array of strings. Each string in this array MUST be one of the "availableAuthMethods" listed below.
            - "scopes": An array of strings. Each string in this array MUST be one of the "availableScopes" listed below.

            Here is an example of a valid JSON object:
            ```json
            {jsonExample}
            ```

            Available authorization grant types are:
            {availableGrantTypes}

            Available client authentication methods are:
            {availableAuthMethods}

            Available scopes are:
            {availableScopes}

            Now, process the following user request and generate the corresponding JSON object. Ensure the output is only the raw JSON, without any extra text or markdown formatting.
            User Request: "{userDescription}"
            """;

        PromptTemplate promptTemplate = new PromptTemplate(promptText);
        Map<String, Object> promptData = Map.of(
                "jsonExample", jsonExample,
                "availableGrantTypes", availableGrantTypes.toString(),
                "availableAuthMethods", availableAuthMethods.toString(),
                "availableScopes", availableScopes.toString(),
                "userDescription", userDescription
        );
        System.out.println("Prompt data: " + promptData);
        String jsonResponse = chatClient.prompt(promptTemplate.create(promptData))
                .call()
                .content();

        jsonResponse = extractJsonFromMarkdown(jsonResponse);

        try {
            return objectMapper.readValue(jsonResponse, AiClientResponseDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse AI response to AiClientResponseDTO", e);
        }
    }

    private String getClientJsonExample() {
        ClientCreateRequest example = new ClientCreateRequest();
        example.setClientName("ExampleWebAppClient");
        example.setDescription("A client for a typical web application.");
        example.setRedirectUris(Set.of("http://localhost:8080/authorized", "http://localhost:8080/login/oauth2/code/reg-client"));
        example.setAuthorizationGrantTypes(Set.of("authorization_code", "refresh_token"));
        example.setClientAuthenticationMethods(Set.of("client_secret_basic"));
        example.setScopes(List.of("openid", "profile"));
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(example);
        } catch (JsonProcessingException e) {
            return "{}"; // Fallback to empty JSON
        }
    }

    private String extractJsonFromMarkdown(String markdownText) {
        Pattern pattern = Pattern.compile("```json\\s*(.*?)\\s*```", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(markdownText);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            // Fallback if markdown is not found, try to find the first { and last }
            int firstBrace = markdownText.indexOf('{');
            int lastBrace = markdownText.lastIndexOf('}');
            if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
                return markdownText.substring(firstBrace, lastBrace + 1);
            }
        }
        return markdownText; // Return original if no JSON structure is found
    }
}
