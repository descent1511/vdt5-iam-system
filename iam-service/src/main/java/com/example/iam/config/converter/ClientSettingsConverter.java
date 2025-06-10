package com.example.iam.config.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;

import java.util.List;
import java.util.Map;

@Converter
public class ClientSettingsConverter implements AttributeConverter<ClientSettings, String> {

    private final ObjectMapper objectMapper = createObjectMapper();

    @Override
    public String convertToDatabaseColumn(ClientSettings attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute.getSettings());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Error converting ClientSettings to JSON", ex);
        }
    }

    @Override
    public ClientSettings convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            Map<String, Object> settings = objectMapper.readValue(dbData, new TypeReference<>() {});
            return ClientSettings.withSettings(settings).build();
        } catch (Exception ex) {
            throw new IllegalArgumentException("Error converting JSON to ClientSettings", ex);
        }
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        ClassLoader classLoader = ClientSettingsConverter.class.getClassLoader();
        List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
        objectMapper.registerModules(securityModules);
        objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
        return objectMapper;
    }
} 