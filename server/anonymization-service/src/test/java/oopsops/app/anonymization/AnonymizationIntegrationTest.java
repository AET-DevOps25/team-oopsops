package oopsops.app.anonymization;

import com.fasterxml.jackson.databind.ObjectMapper;
import oopsops.app.anonymization.models.AnonymizationRequestBody;
import oopsops.app.anonymization.models.ChangedTerm;
import oopsops.app.anonymization.dto.AnonymizationDto;
import oopsops.app.anonymization.entity.AnonymizationEntity;
import oopsops.app.anonymization.repository.AnonymizationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test") 
@Transactional
class AnonymizationIntegrationTest {

    @TestConfiguration
    static class JwtDecoderConfig {
        @Bean
        public JwtDecoder jwtDecoder() {
            return token -> Jwt.withTokenValue(token)
                    .header("alg", "none")
                    .claim("sub", TEST_USER.toString())
                    .build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AnonymizationRepository anonymizationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID documentId;


    private static final UUID TEST_USER = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @BeforeEach
    void cleanDatabase() {
        anonymizationRepository.deleteAll();
        documentId = UUID.randomUUID();
    }


    @Test
    void addNewAnonymization_ShouldSaveAndReturnDto() throws Exception {
        AnonymizationRequestBody request = new AnonymizationRequestBody(
                "John likes pizza", 
                "Person A likes pizza",
                "medium",
                List.of(new ChangedTerm("John", "Person A"))
        );

        mockMvc.perform(post("/api/v1/anonymization/" + documentId + "/add")
                .with(jwt().jwt(jwt -> jwt.subject(TEST_USER.toString())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.documentId").value(documentId.toString()))
            .andExpect(jsonPath("$.originalText").value("John likes pizza"))
            .andExpect(jsonPath("$.anonymizedText").value("Person A likes pizza"))
            .andExpect(jsonPath("$.anonymization_level").value("medium"))
            .andExpect(jsonPath("$.changedTerms[0].original").value("John"))
            .andExpect(jsonPath("$.changedTerms[0].anonymized").value("Person A"));

        // Verify saved in DB
        List<AnonymizationDto> saved = anonymizationRepository.findAll().stream()
            .map(entity -> AnonymizationDto.fromDao(entity))
            .toList();
        assertThat(saved).hasSize(1);
        assertThat(saved.get(0).documentId()).isEqualTo(documentId);
        assertThat(saved.get(0).userId()).isEqualTo(TEST_USER);
    }

    @Test
    void updateExistingAnonymization_ShouldOverwriteExisting() throws Exception {
        
        var existingEntity = anonymizationRepository.save(
            new AnonymizationEntity(
                null,
                OffsetDateTime.now(),
                documentId,
                TEST_USER,
                "John likes pizza",
                "Person A likes pizza",
                "medium",
                List.of(new ChangedTerm("John", "Person A"))
            )
        );

        AnonymizationRequestBody updatedRequest = new AnonymizationRequestBody(
                "John likes pizza",
                "Person ABC likes pizza",
                "high",
                List.of(new ChangedTerm("John", "Person ABC"))
        );

        mockMvc.perform(post("/api/v1/anonymization/" + documentId + "/add")
                .with(jwt().jwt(jwt -> jwt.subject(TEST_USER.toString())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(existingEntity.getId().toString()))
            .andExpect(jsonPath("$.originalText").value("John likes pizza"))
            .andExpect(jsonPath("$.anonymizedText").value("Person ABC likes pizza"))
            .andExpect(jsonPath("$.anonymization_level").value("high"));

        var updatedEntity = anonymizationRepository.findById(existingEntity.getId()).orElseThrow();
        assertThat(updatedEntity.getOriginalText()).isEqualTo("John likes pizza");
        assertThat(updatedEntity.getAnonymizedText()).isEqualTo("Person ABC likes pizza");
        assertThat(updatedEntity.getAnonymization_level()).isEqualTo("high");
    }

    @Test
    void getAllAnonymizations_ShouldReturnAllForUser() throws Exception {
        anonymizationRepository.save(new AnonymizationEntity(
                null, OffsetDateTime.now(), UUID.randomUUID(), TEST_USER,
                "Text1", "Anonymized1", "low", List.of()));
        anonymizationRepository.save(new AnonymizationEntity(
                null, OffsetDateTime.now(), UUID.randomUUID(), TEST_USER,
                "Text2", "Anonymized2", "medium", List.of()));

        mockMvc.perform(get("/api/v1/anonymization")
                .with(jwt().jwt(jwt -> jwt.subject(TEST_USER.toString())))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));
    }
}
