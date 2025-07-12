package oopsops.app.anonymization.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import oopsops.app.anonymization.entity.AnonymizationEntity;
import oopsops.app.anonymization.models.ChangedTerm;

public record AnonymizationDto(
    UUID id,
    OffsetDateTime created,
    UUID documentId,
    UUID userId,
    String originalText,
    String anonymizedText,
    String anonymization_level,
    List<ChangedTerm> changedTerms) {
    public static AnonymizationDto fromDao(AnonymizationEntity entity) {
        return new AnonymizationDto(
            entity.getId(),
            entity.getCreated(),
            entity.getDocumentId(),
            entity.getUserId(),
            entity.getOriginalText(),
            entity.getAnonymizedText(),
            entity.getAnonymization_level(),
            entity.getChangedTerms()
        );
    }

    public AnonymizationEntity toDao() {
        AnonymizationEntity entity = new AnonymizationEntity();
        if (created != null) {
            entity.setCreated(created);
        }
        if (this.id != null) {
            System.out.println("Setting ID: " + this.id);
            entity.setId(this.id);
        }
        entity.setDocumentId(documentId);
        entity.setUserId(userId);
        entity.setOriginalText(originalText);
        entity.setAnonymizedText(anonymizedText);
        entity.setAnonymization_level(anonymization_level);
        entity.setChangedTerms(changedTerms);
        return entity;
    }
}