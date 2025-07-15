package oopsops.app.anonymization;

import com.fasterxml.jackson.databind.ObjectMapper;
import oopsops.app.anonymization.controller.AnonymizationController;
import oopsops.app.anonymization.dto.AnonymizationDto;
import oopsops.app.anonymization.entity.AnonymizationEntity;
import oopsops.app.anonymization.models.AnonymizationRequestBody;
import oopsops.app.anonymization.models.ChangedTerm;
import oopsops.app.anonymization.models.ReplacementRequest;
import oopsops.app.anonymization.service.AnonymizationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@WebMvcTest(AnonymizationController.class)
public class AnonymizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnonymizationService anonymizationService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID userId;
    private UUID documentId;
    private AnonymizationDto dto;
    private AnonymizationEntity entity;

    @BeforeEach
    void setUp() {
        userId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        documentId = UUID.randomUUID();
        dto = new AnonymizationDto(
                UUID.randomUUID(),
                OffsetDateTime.now(),
                documentId,
                userId,
                "John wants to go outside",
                "Person A wants to go outside",
                "medium",
                List.of(new ChangedTerm("John", "Person A")));
        entity = new AnonymizationEntity(
                dto.id(),
                dto.created(),
                dto.documentId(),
                dto.userId(),
                dto.originalText(),
                dto.anonymizedText(),
                dto.anonymization_level(),
                dto.changedTerms());
    }

    @Test
    void getAllAnonymizations_ShouldReturnList() throws Exception {
        when(anonymizationService.getAllAnonymizations(userId)).thenReturn(List.of(entity));

        mockMvc.perform(get("/api/v1/anonymization")
                .with(csrf())
                .with(jwt().jwt(jwt -> jwt.subject(userId.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(dto.id().toString()));

        verify(anonymizationService).getAllAnonymizations(userId);
    }

    @Test
    void getAllAnonymizations_WhenNoDocuments_ShouldReturnEmptyList() throws Exception {
        when(anonymizationService.getAllAnonymizations(userId)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/v1/anonymization")
                .with(csrf())
                .with(jwt().jwt(jwt -> jwt.subject(userId.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(anonymizationService, times(1)).getAllAnonymizations(userId);
    }

    @Test
    void add_ShouldSaveAnonymization() throws Exception {
        AnonymizationRequestBody body = new AnonymizationRequestBody(
                "John wants to go outside", "Person A wants to go outside", "medium",
                List.of(new ChangedTerm("John", "Person A")));

        when(anonymizationService.findByDocumentId(documentId)).thenReturn(Optional.empty());
        when(anonymizationService.save(any())).thenReturn(dto);

        mockMvc.perform(post("/api/v1/anonymization/" + documentId + "/add")
                .with(jwt().jwt(jwt -> jwt.subject(userId.toString())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentId").value(documentId.toString()));

        verify(anonymizationService).save(any());
    }

    @Test
    void add_ShouldUpdateIfDocumentExists() throws Exception {
        AnonymizationRequestBody body = new AnonymizationRequestBody(
                "John wants to go outside", "Person J wants to go outside", "medium",
                List.of(new ChangedTerm("John", "Person J")));

        when(anonymizationService.findByDocumentId(documentId)).thenReturn(Optional.of(dto));

        AnonymizationDto updatedDto = new AnonymizationDto(
                dto.id(),
                OffsetDateTime.now(),
                documentId,
                userId,
                body.originalText(),
                body.anonymizedText(),
                body.level(),
                body.changedTerms());

        when(anonymizationService.save(any())).thenReturn(updatedDto);

        mockMvc.perform(post("/api/v1/anonymization/" + documentId + "/add")
                .with(csrf())
                .with(jwt().jwt(jwt -> jwt.subject(userId.toString())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentId").value(documentId.toString()))
                .andExpect(jsonPath("$.originalText").value(body.originalText()))
                .andExpect(jsonPath("$.anonymizedText").value(body.anonymizedText()))
                .andExpect(jsonPath("$.changedTerms[0].original").value("John"))
                .andExpect(jsonPath("$.changedTerms[0].anonymized").value("Person J"));

        verify(anonymizationService).save(any());
    }

    @Test
    void replace_ShouldReturnAnonymizedText() throws Exception {
        ReplacementRequest req = new ReplacementRequest();
        req.setOriginalText("John met Johnathan.");
        req.setChangedTerms(List.of(
                new ChangedTerm("Johnathan", "Person B"),
                new ChangedTerm("John", "Person A")));
        mockMvc.perform(post("/api/v1/anonymization/replace")
                .with(jwt().jwt(jwt -> jwt.subject(userId.toString())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().string("Person A met Person B."));
    }

}
