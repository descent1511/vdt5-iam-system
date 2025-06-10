package com.example.iam.config.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.util.List;
import java.util.Map;

@Converter
public class TokenSettingsConverter implements AttributeConverter<TokenSettings, String> {

    private final ObjectMapper objectMapper = createObjectMapper();

    @Override
    public String convertToDatabaseColumn(TokenSettings attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute.getSettings());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Error converting TokenSettings to JSON", ex);
        }
    }

    @Override
    public TokenSettings convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            Map<String, Object> settings = objectMapper.readValue(dbData, new TypeReference<>() {});
            return TokenSettings.withSettings(settings).build();
        } catch (Exception ex) {
            throw new IllegalArgumentException("Error converting JSON to TokenSettings", ex);
        }
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        ClassLoader classLoader = TokenSettingsConverter.class.getClassLoader();
        List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
        objectMapper.registerModules(securityModules);
        objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
        return objectMapper;
    }
}