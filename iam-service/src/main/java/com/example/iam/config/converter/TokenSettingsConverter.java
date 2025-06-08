package com.example.iam.config.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;

@Component
@Converter
@RequiredArgsConstructor
public class TokenSettingsConverter implements AttributeConverter<TokenSettings, String> {

    private final ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(TokenSettings attribute) {
        try {
            return objectMapper.writeValueAsString(attribute.getSettings());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Error converting TokenSettings to JSON", ex);
        }
    }

    @Override
    public TokenSettings convertToEntityAttribute(String dbData) {
        try {
            Map<String, Object> settings = objectMapper.readValue(dbData, new TypeReference<>() {});
            // Chuyển đổi các giá trị chuỗi ISO-8601 thành Duration
            convertStringToDuration(settings, "settings.token.access-token-time-to-live");
            convertStringToDuration(settings, "settings.token.refresh-token-time-to-live");
            convertStringToDuration(settings, "settings.token.authorization-code-time-to-live");
            convertStringToDuration(settings, "settings.token.device-code-time-to-live");
            return TokenSettings.withSettings(settings).build();
        } catch (Exception ex) {
            throw new IllegalArgumentException("Error converting JSON to TokenSettings", ex);
        }
    }

    private void convertStringToDuration(Map<String, Object> settings, String key) {
        if (settings.containsKey(key) && settings.get(key) instanceof String) {
            String value = (String) settings.get(key);
            try {
                settings.put(key, Duration.parse(value));
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid Duration format for key: " + key + ", value: " + value, e);
            }
        }
    }
}