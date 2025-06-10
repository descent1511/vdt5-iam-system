package com.example.iam.ai;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.iam.dto.AiRoleResponseDTO;
import com.example.iam.dto.AiClientResponseDTO;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiGenerationService aiGenerationService;

    @PostMapping("/generate-entity")
    public ResponseEntity<Object> generateEntity(@Valid @RequestBody AiGenerationRequest request) {
        Object generatedEntity = aiGenerationService.generateEntity(request.getEntityType(), request.getUserDescription());
        return ResponseEntity.ok(generatedEntity);
    }
} 