package oopsops.app.anonymization;

import com.fasterxml.jackson.databind.ObjectMapper;
import oopsops.app.anonymization.models.AnonymizationRequestBody;
import oopsops.app.anonymization.models.ChangedTerm;
import oopsops.app.anonymization.dto.AnonymizationDto;
import oopsops.app.anonymization.repository.AnonymizationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") 
class AnonymizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AnonymizationRepository anonymizationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID userId;
    private UUID documentId;

    @BeforeEach
    void setUp() {
        anonymizationRepository.deleteAll();

        userId = UUID.fromString("00000000-0000-0000-0000-000000000000");
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
                .with(jwt().jwt(jwt -> jwt.subject(userId.toString())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.documentId").value(documentId.toString()))
            .andExpect(jsonPath("$.originalText").value("John likes pizza"))
            .andExpect(jsonPath("$.anonymizedText").value("Person A likes pizza"))
            .andExpect(jsonPath("$.anonymization_level").value("medium"))
            .andExpect(jsonPath("$.changedTerms[0].originalTerm").value("John"))
            .andExpect(jsonPath("$.changedTerms[0].anonymizedTerm").value("Person A"));

        // Verify saved in DB
        List<AnonymizationDto> saved = anonymizationRepository.findAll().stream()
            .map(entity -> AnonymizationDto.fromDao(entity))
            .toList();
        assertThat(saved).hasSize(1);
        assertThat(saved.get(0).getDocumentId()).isEqualTo(documentId);
        assertThat(saved.get(0).getUserId()).isEqualTo(userId);
    }

    @Test
    void updateExistingAnonymization_ShouldOverwriteExisting() throws Exception {
        // First insert an existing anonymization
        var existingEntity = anonymizationRepository.save(
            new AnonymizationEntity(
                null,
                OffsetDateTime.now(),
                documentId,
                userId,
                "John likes pizza",
                "Person A likes pizza",
                "medium",
                List.of(new ChangedTerm("John", "Person A"))
            )
        );

        AnonymizationRequestBody updatedRequest = new AnonymizationRequestBody(
                "John likes pasta",
                "Person A likes pasta",
                "high",
                List.of(new ChangedTerm("John", "Person A"))
        );

        mockMvc.perform(post("/api/v1/anonymization/" + documentId + "/add")
                .with(jwt().jwt(jwt -> jwt.subject(userId.toString())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(existingEntity.getId().toString()))
            .andExpect(jsonPath("$.originalText").value("John likes pasta"))
            .andExpect(jsonPath("$.anonymizedText").value("Person A likes pasta"))
            .andExpect(jsonPath("$.anonymization_level").value("high"));

        // Verify the DB was updated (same ID, updated fields)
        var updatedEntity = anonymizationRepository.findById(existingEntity.getId()).orElseThrow();
        assertThat(updatedEntity.getOriginalText()).isEqualTo("John likes pasta");
        assertThat(updatedEntity.getAnonymizedText()).isEqualTo("Person A likes pasta");
        assertThat(updatedEntity.getAnonymization_level()).isEqualTo("high");
    }

    @Test
    void getAllAnonymizations_ShouldReturnAllForUser() throws Exception {
        // Insert two anonymizations for the user
        anonymizationRepository.save(new AnonymizationEntity(
                null, OffsetDateTime.now(), UUID.randomUUID(), userId,
                "Text1", "Anonymized1", "low", List.of()));
        anonymizationRepository.save(new AnonymizationEntity(
                null, OffsetDateTime.now(), UUID.randomUUID(), userId,
                "Text2", "Anonymized2", "medium", List.of()));

        mockMvc.perform(get("/api/v1/anonymization")
                .with(jwt().jwt(jwt -> jwt.subject(userId.toString())))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));
    }
}
