package com.example.iam.config.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Converter
@RequiredArgsConstructor
public class ClientSettingsConverter implements AttributeConverter<ClientSettings, String> {

    private final ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(ClientSettings attribute) {
        try {
            return objectMapper.writeValueAsString(attribute.getSettings());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Error converting ClientSettings to JSON", ex);
        }
    }

    @Override
    public ClientSettings convertToEntityAttribute(String dbData) {
        try {
            Map<String, Object> settings = objectMapper.readValue(dbData, new TypeReference<>() {});
            return ClientSettings.withSettings(settings).build();
        } catch (Exception ex) {
            throw new IllegalArgumentException("Error converting JSON to ClientSettings", ex);
        }
    }
} 